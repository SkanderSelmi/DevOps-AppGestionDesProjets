package tn.esprit.backend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.backend.entity.Projet;
import tn.esprit.backend.repository.ProjetRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjetServiceImplTest {

    @Mock
    private ProjetRepository projetRepository;

    @InjectMocks
    private ProjetServiceImpl projetService;

    private Projet projet;

    @BeforeEach
    void setUp() {
        projet = Projet.builder()
                .id(1L)
                .sujet("Migration Cloud")
                .build();
    }

    @Test
    void addProjet_shouldSaveAndReturnProjet() {
        when(projetRepository.save(projet)).thenReturn(projet);

        Projet result = projetService.addProjet(projet);

        assertNotNull(result);
        assertEquals("Migration Cloud", result.getSujet());
        verify(projetRepository, times(1)).save(projet);
    }

    @Test
    void updateProjet_shouldSaveAndReturnUpdatedProjet() {
        projet.setSujet("Migration Cloud v2");
        when(projetRepository.save(projet)).thenReturn(projet);

        Projet result = projetService.updateProjet(projet);

        assertEquals("Migration Cloud v2", result.getSujet());
        verify(projetRepository, times(1)).save(projet);
    }

    @Test
    void deleteProjet_shouldCallRepositoryDeleteById() {
        doNothing().when(projetRepository).deleteById(1L);

        projetService.deleteProjet(1L);

        verify(projetRepository, times(1)).deleteById(1L);
    }

    @Test
    void getProjetById_whenFound_shouldReturnProjet() {
        when(projetRepository.findById(1L)).thenReturn(Optional.of(projet));

        Projet result = projetService.getProjetById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getProjetById_whenNotFound_shouldReturnNull() {
        when(projetRepository.findById(99L)).thenReturn(Optional.empty());

        Projet result = projetService.getProjetById(99L);

        assertNull(result);
    }

    @Test
    void getAllProjets_shouldReturnListOfProjets() {
        Projet projet2 = Projet.builder().id(2L).sujet("Refonte UI").build();
        when(projetRepository.findAll()).thenReturn(Arrays.asList(projet, projet2));

        List<Projet> result = projetService.getAllProjets();

        assertEquals(2, result.size());
        verify(projetRepository, times(1)).findAll();
    }
}