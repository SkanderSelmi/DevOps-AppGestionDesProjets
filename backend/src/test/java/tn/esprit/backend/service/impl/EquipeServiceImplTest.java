package tn.esprit.backend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.backend.entity.Entreprise;
import tn.esprit.backend.entity.Equipe;
import tn.esprit.backend.entity.Projet;
import tn.esprit.backend.repository.EntrepriseRepository;
import tn.esprit.backend.repository.EquipeRepository;
import tn.esprit.backend.repository.ProjetRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipeServiceImplTest {

    @Mock
    private EquipeRepository equipeRepository;

    @Mock
    private EntrepriseRepository entrepriseRepository;

    @Mock
    private ProjetRepository projetRepository;

    @InjectMocks
    private EquipeServiceImpl equipeService;

    private Equipe equipe;
    private Entreprise entreprise;
    private Projet projet;

    @BeforeEach
    void setUp() {
        entreprise = Entreprise.builder().id(1L).nom("Esprit Corp").adresse("Ariana").build();

        equipe = Equipe.builder()
                .id(1L)
                .nom("Team Alpha")
                .specialite("DevOps")
                .projets(new ArrayList<>())
                .build();

        projet = new Projet();
        projet.setId(1L);
    }

    @Test
    void addEquipe_shouldSaveAndReturnEquipe() {
        when(equipeRepository.save(equipe)).thenReturn(equipe);

        Equipe result = equipeService.addEquipe(equipe);

        assertNotNull(result);
        assertEquals("Team Alpha", result.getNom());
        verify(equipeRepository, times(1)).save(equipe);
    }

    @Test
    void updateEquipe_shouldSaveAndReturnUpdatedEquipe() {
        equipe.setSpecialite("Cloud");
        when(equipeRepository.save(equipe)).thenReturn(equipe);

        Equipe result = equipeService.updateEquipe(equipe);

        assertEquals("Cloud", result.getSpecialite());
        verify(equipeRepository, times(1)).save(equipe);
    }

    @Test
    void deleteEquipe_shouldCallRepositoryDeleteById() {
        doNothing().when(equipeRepository).deleteById(1L);

        equipeService.deleteEquipe(1L);

        verify(equipeRepository, times(1)).deleteById(1L);
    }

    @Test
    void getEquipeById_whenFound_shouldReturnEquipe() {
        when(equipeRepository.findById(1L)).thenReturn(Optional.of(equipe));

        Equipe result = equipeService.getEquipeById(1L);

        assertNotNull(result);
        assertEquals("Team Alpha", result.getNom());
    }

    @Test
    void getEquipeById_whenNotFound_shouldReturnNull() {
        when(equipeRepository.findById(42L)).thenReturn(Optional.empty());

        Equipe result = equipeService.getEquipeById(42L);

        assertNull(result);
    }

    @Test
    void getAllEquipes_shouldReturnListOfEquipes() {
        Equipe equipe2 = Equipe.builder().id(2L).nom("Team Beta").specialite("Frontend").build();
        when(equipeRepository.findAll()).thenReturn(Arrays.asList(equipe, equipe2));

        List<Equipe> result = equipeService.getAllEquipes();

        assertEquals(2, result.size());
        verify(equipeRepository, times(1)).findAll();
    }

    @Test
    void getEquipesByEntreprise_shouldReturnFilteredList() {
        when(equipeRepository.findByEntrepriseId(1L)).thenReturn(List.of(equipe));

        List<Equipe> result = equipeService.getEquipesByEntreprise(1L);

        assertEquals(1, result.size());
        verify(equipeRepository, times(1)).findByEntrepriseId(1L);
    }

    @Test
    void assignEquipeToEntreprise_shouldSetEntrepriseAndSave() {
        when(equipeRepository.findById(1L)).thenReturn(Optional.of(equipe));
        when(entrepriseRepository.findById(1L)).thenReturn(Optional.of(entreprise));
        when(equipeRepository.save(equipe)).thenReturn(equipe);

        Equipe result = equipeService.assignEquipeToEntreprise(1L, 1L);

        assertEquals(entreprise, result.getEntreprise());
        verify(equipeRepository, times(1)).save(equipe);
    }

    @Test
    void assignEquipeToProjet_shouldAddProjetAndSave() {
        when(equipeRepository.findById(1L)).thenReturn(Optional.of(equipe));
        when(projetRepository.findById(1L)).thenReturn(Optional.of(projet));
        when(equipeRepository.save(equipe)).thenReturn(equipe);

        Equipe result = equipeService.assignEquipeToProjet(1L, 1L);

        assertTrue(result.getProjets().contains(projet));
        verify(equipeRepository, times(1)).save(equipe);
    }
}