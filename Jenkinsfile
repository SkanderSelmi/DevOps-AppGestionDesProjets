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
                    // Compilation seule : le jar sera produit à l'étape Package, après le Quality Gate
                    sh 'mvn clean compile'
                }
            }
        }

        stage('Start Test Database') {
            steps {
                sh 'docker compose up -d mysql'
                sh '''
                    until docker exec mysql-db mysqladmin ping -h localhost -uroot -proot --silent; do
                        echo "En attente de MySQL..."
                        sleep 2
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
                    sh 'mvn test'
                }
            }
            post {
                always {
                    junit 'backend/target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                dir('backend') {
                    // 'SonarQube' = nom du serveur configuré dans Jenkins (Configurer le système)
                    withSonarQubeEnv('SonarQube') {
                        sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.host.url=$SONAR_HOST_URL -Dsonar.token=$SONAR_AUTH_TOKEN'
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                // Nécessite le webhook SonarQube -> Jenkins (voir étape dédiée)
                timeout(time: 2, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Package') {
            steps {
                dir('backend') {
                    sh 'mvn package -DskipTests'
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
            echo 'Pipeline terminé avec succès : qualité validée, images poussées, application déployée.'
        }
        failure {
            echo 'Le pipeline a échoué, vérifier la Console Output.'
        }
    }
}