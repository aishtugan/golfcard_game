package org.example.golfsolitaire.resources;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CardResourcesTest {
    @Test
    void cardImagesAreAvailableFromClasspath() {
        assertNotNull(getClass().getResource("/cards/AS.png"));
        assertNotNull(getClass().getResource("/cards/10H.png"));
        assertNotNull(getClass().getResource("/cards/QC.png"));
        assertNotNull(getClass().getResource("/cards/back.png"));
    }
}
