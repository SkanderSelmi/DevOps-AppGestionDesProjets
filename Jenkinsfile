pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-creds')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Backend') {
            steps {
                dir('backend') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Start Test Database') {
            steps {
                // Démarre uniquement MySQL, nécessaire pour BackendApplicationTests
                // (le vrai contexte Spring a besoin d'une vraie connexion DB).
                sh 'docker compose up -d mysql'
                // Attend que MySQL soit prêt à accepter des connexions avant de lancer les tests.
                sh '''
                    until docker exec mysql-db mysqladmin ping -h localhost -uroot -proot --silent; do
                        echo "En attente de MySQL..."
                        sleep 3
                    done
                '''
            }
        }

        stage('Test Backend') {
            environment {
                DB_HOST = 'localhost'
                DB_PORT = '3307'
                DB_NAME = 'devops_db'
                DB_USER = 'devops'
                DB_PASSWORD = 'devops123'
            }
            steps {
                dir('backend') {
                    // Tous les tests, y compris BackendApplicationTests, contre la vraie base MySQL.
                    sh 'mvn test'
                }
            }
            post {
                always {
                    junit 'backend/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build Frontend') {
            steps {
                dir('frontend') {
                    sh 'npm install'
                    sh 'npm run build'
                }
            }
        }

        stage('Archive') {
            steps {
                archiveArtifacts artifacts: 'backend/target/*.jar', fingerprint: true
                archiveArtifacts artifacts: 'frontend/dist/**', fingerprint: true, allowEmptyArchive: true
            }
        }

        stage('Build & Push Docker Images') {
            steps {
                sh 'docker compose build backend frontend'
                sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
                sh 'docker tag devops-backend:latest $DOCKERHUB_CREDENTIALS_USR/devops-backend:latest'
                sh 'docker tag devops-frontend:latest $DOCKERHUB_CREDENTIALS_USR/devops-frontend:latest'
                sh 'docker tag devops-backend:latest $DOCKERHUB_CREDENTIALS_USR/devops-backend:${BUILD_NUMBER}'
                sh 'docker tag devops-frontend:latest $DOCKERHUB_CREDENTIALS_USR/devops-frontend:${BUILD_NUMBER}'
                sh 'docker push $DOCKERHUB_CREDENTIALS_USR/devops-backend:latest'
                sh 'docker push $DOCKERHUB_CREDENTIALS_USR/devops-frontend:latest'
                sh 'docker push $DOCKERHUB_CREDENTIALS_USR/devops-backend:${BUILD_NUMBER}'
                sh 'docker push $DOCKERHUB_CREDENTIALS_USR/devops-frontend:${BUILD_NUMBER}'
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                sh 'docker compose down --remove-orphans'
                sh 'docker compose up -d --build'
            }
        }
    }

    post {
        always {
            sh 'docker logout || true'
        }
        success {
            echo 'Pipeline terminé avec succès — images poussées sur Docker Hub et application déployée.'
        }
        failure {
            echo 'Le pipeline a échoué — vérifier la Console Output.'
        }
    }
}