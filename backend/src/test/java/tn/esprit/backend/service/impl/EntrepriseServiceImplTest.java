package tn.esprit.backend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.backend.entity.Entreprise;
import tn.esprit.backend.repository.EntrepriseRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntrepriseServiceImplTest {

    @Mock
    private EntrepriseRepository entrepriseRepository;

    @InjectMocks
    private EntrepriseServiceImpl entrepriseService;

    private Entreprise entreprise;

    @BeforeEach
    void setUp() {
        entreprise = Entreprise.builder()
                .id(1L)
                .nom("Esprit Corp")
                .adresse("Ariana, Tunisie")
                .build();
    }

    @Test
    void addEntreprise_shouldSaveAndReturnEntreprise() {
        when(entrepriseRepository.save(entreprise)).thenReturn(entreprise);

        Entreprise result = entrepriseService.addEntreprise(entreprise);

        assertNotNull(result);
        assertEquals("Esprit Corp", result.getNom());
        verify(entrepriseRepository, times(1)).save(entreprise);
    }

    @Test
    void updateEntreprise_shouldSaveAndReturnUpdatedEntreprise() {
        entreprise.setAdresse("Nouvelle adresse");
        when(entrepriseRepository.save(entreprise)).thenReturn(entreprise);

        Entreprise result = entrepriseService.updateEntreprise(entreprise);

        assertEquals("Nouvelle adresse", result.getAdresse());
        verify(entrepriseRepository, times(1)).save(entreprise);
    }

    @Test
    void deleteEntreprise_shouldCallRepositoryDeleteById() {
        doNothing().when(entrepriseRepository).deleteById(1L);

        entrepriseService.deleteEntreprise(1L);

        verify(entrepriseRepository, times(1)).deleteById(1L);
    }

    @Test
    void getEntrepriseById_whenFound_shouldReturnEntreprise() {
        when(entrepriseRepository.findById(1L)).thenReturn(Optional.of(entreprise));

        Entreprise result = entrepriseService.getEntrepriseById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getEntrepriseById_whenNotFound_shouldReturnNull() {
        when(entrepriseRepository.findById(99L)).thenReturn(Optional.empty());

        Entreprise result = entrepriseService.getEntrepriseById(99L);

        assertNull(result);
    }

    @Test
    void getAllEntreprises_shouldReturnListOfEntreprises() {
        Entreprise entreprise2 = Entreprise.builder().id(2L).nom("Autre Corp").adresse("Tunis").build();
        when(entrepriseRepository.findAll()).thenReturn(Arrays.asList(entreprise, entreprise2));

        List<Entreprise> result = entrepriseService.getAllEntreprises();

        assertEquals(2, result.size());
        verify(entrepriseRepository, times(1)).findAll();
    }
}