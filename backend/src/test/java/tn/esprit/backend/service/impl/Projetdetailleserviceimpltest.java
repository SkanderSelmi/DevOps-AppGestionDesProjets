package tn.esprit.backend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.backend.entity.Projet;
import tn.esprit.backend.entity.ProjetDetaille;
import tn.esprit.backend.repository.ProjetDetailleRepository;
import tn.esprit.backend.repository.ProjetRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjetDetailleServiceImplTest {

    @Mock
    private ProjetDetailleRepository projetDetailleRepository;

    @Mock
    private ProjetRepository projetRepository;

    @InjectMocks
    private ProjetDetailleServiceImpl projetDetailleService;

    private ProjetDetaille projetDetaille;
    private Projet projet;

    @BeforeEach
    void setUp() {
        projet = Projet.builder().id(1L).sujet("Migration Cloud").build();

        projetDetaille = ProjetDetaille.builder()
                .id(1L)
                .description("Mise en place CI/CD")
                .technologie("Jenkins")
                .coutProvisoire(1500.0)
                .dateDebut(LocalDate.of(2026, 9, 1))
                .build();
    }

    @Test
    void addProjetDetaille_shouldSaveAndReturnProjetDetaille() {
        when(projetDetailleRepository.save(projetDetaille)).thenReturn(projetDetaille);

        ProjetDetaille result = projetDetailleService.addProjetDetaille(projetDetaille);

        assertNotNull(result);
        assertEquals("Jenkins", result.getTechnologie());
        verify(projetDetailleRepository, times(1)).save(projetDetaille);
    }

    @Test
    void updateProjetDetaille_shouldSaveAndReturnUpdatedProjetDetaille() {
        projetDetaille.setCoutProvisoire(2000.0);
        when(projetDetailleRepository.save(projetDetaille)).thenReturn(projetDetaille);

        ProjetDetaille result = projetDetailleService.updateProjetDetaille(projetDetaille);

        assertEquals(2000.0, result.getCoutProvisoire());
        verify(projetDetailleRepository, times(1)).save(projetDetaille);
    }

    @Test
    void deleteProjetDetaille_shouldCallRepositoryDeleteById() {
        doNothing().when(projetDetailleRepository).deleteById(1L);

        projetDetailleService.deleteProjetDetaille(1L);

        verify(projetDetailleRepository, times(1)).deleteById(1L);
    }

    @Test
    void getProjetDetailleById_whenFound_shouldReturnProjetDetaille() {
        when(projetDetailleRepository.findById(1L)).thenReturn(Optional.of(projetDetaille));

        ProjetDetaille result = projetDetailleService.getProjetDetailleById(1L);

        assertNotNull(result);
        assertEquals("Mise en place CI/CD", result.getDescription());
    }

    @Test
    void getProjetDetailleById_whenNotFound_shouldReturnNull() {
        when(projetDetailleRepository.findById(42L)).thenReturn(Optional.empty());

        ProjetDetaille result = projetDetailleService.getProjetDetailleById(42L);

        assertNull(result);
    }

    @Test
    void getAllProjetsDetailles_shouldReturnList() {
        ProjetDetaille pd2 = ProjetDetaille.builder().id(2L).description("Autre").technologie("Docker").build();
        when(projetDetailleRepository.findAll()).thenReturn(Arrays.asList(projetDetaille, pd2));

        List<ProjetDetaille> result = projetDetailleService.getAllProjetsDetailles();

        assertEquals(2, result.size());
        verify(projetDetailleRepository, times(1)).findAll();
    }

    @Test
    void getProjetDetaillesByProjet_shouldReturnFilteredList() {
        when(projetDetailleRepository.findByProjetId(1L)).thenReturn(List.of(projetDetaille));

        List<ProjetDetaille> result = projetDetailleService.getProjetDetaillesByProjet(1L);

        assertEquals(1, result.size());
        verify(projetDetailleRepository, times(1)).findByProjetId(1L);
    }

    @Test
    void assignProjetDetailleToProjet_shouldSetProjetAndSave() {
        when(projetDetailleRepository.findById(1L)).thenReturn(Optional.of(projetDetaille));
        when(projetRepository.findById(1L)).thenReturn(Optional.of(projet));
        when(projetDetailleRepository.save(projetDetaille)).thenReturn(projetDetaille);

        ProjetDetaille result = projetDetailleService.assignProjetDetailleToProjet(1L, 1L);

        assertEquals(projet, result.getProjet());
        verify(projetDetailleRepository, times(1)).save(projetDetaille);
    }
}