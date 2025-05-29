// src/main/java/com/example/mobileproject/config/DrugDataLoader.java
package com.example.mobileproject.config;

import com.example.mobileproject.entity.DrugReference;
import com.example.mobileproject.repository.DrugReferenceRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DrugDataLoader {

    private final DrugReferenceRepository repo;

    @PostConstruct
    void init() {
        if (repo.count() > 0) return; // déjà remplie

        repo.saveAll(List.of(
                // 🔹 Douleurs et fièvre (usage courant)
                new DrugReference(null, "Doliprane", "Paracétamol", "N02BE01"), // Fièvre, douleurs
                new DrugReference(null, "Efferalgan", "Paracétamol", "N02BE01"), // Fièvre, douleurs
                new DrugReference(null, "Dafalgan", "Paracétamol", "N02BE01"), // Fièvre, douleurs
                new DrugReference(null, "Nurofen", "Ibuprofène", "M01AE01"), // Douleurs, fièvre, inflammation
                new DrugReference(null, "Ibuprofène", "Ibuprofène", "M01AE01"), // Inflammation, douleurs
                new DrugReference(null, "Aspirine", "Acide acétylsalicylique", "B01AC06"), // Douleurs, fièvre, prévention AVC/IDM

                // 🔹 Affections digestives
                new DrugReference(null, "Spasfon", "Phloroglucinol", "A03AX12"), // Crampes digestives
                new DrugReference(null, "Smecta", "Diosmectite", "A07BC05"), // Diarrhée
                new DrugReference(null, "Imodium", "Lopéramide", "A07DA03"), // Diarrhée aiguë
                new DrugReference(null, "Maalox", "Hydroxyde d’aluminium/magnésium", "A02AD01"), // Reflux gastrique
                new DrugReference(null, "Gaviscon", "Alginate de sodium/Bicarbonate", "A02BX13"), // Reflux acide, brûlures

                // 🔹 Rhumes, ORL
                new DrugReference(null, "Actifed", "Triprolidine/Pseudoéphédrine", "R01BA52"), // Rhume, congestion
                new DrugReference(null, "Rhinadvil", "Ibuprofène/Pseudoéphédrine", "R01BA52"), // Rhume avec douleurs
                new DrugReference(null, "Toplexil", "Oxeladine", "R05DB13"), // Toux sèche
                new DrugReference(null, "Hextril", "Hexétidine", "R02AA02"), // Maux de gorge

                // 🔹 Antibiotiques (infections aiguës)
                new DrugReference(null, "Augmentin", "Amoxicilline/Acide clavulanique", "J01CR02"), // Infections ORL, pulmonaires
                new DrugReference(null, "Clamoxyl", "Amoxicilline", "J01CA04"), // Infections diverses

                // 🔹 Allergies (souvent chroniques)
                new DrugReference(null, "Zyrtec", "Cétirizine", "R06AE07"), // Allergies chroniques ou saisonnières

                // 🔹 Asthme (maladie chronique)
                new DrugReference(null, "Ventoline", "Salbutamol", "R03AC02"), // Crise d’asthme, BPCO

                // 🔹 Hypertension artérielle (maladie chronique)
                new DrugReference(null, "Amlor", "Amlodipine", "C08CA01"), // Tension artérielle, angine de poitrine
                new DrugReference(null, "Coversyl", "Périndopril", "C09AA04"), // Hypertension, insuffisance cardiaque
                new DrugReference(null, "Atacand", "Candésartan", "C09CA06"), // Hypertension, post-infarctus

                // 🔹 Diabète (maladie chronique)
                new DrugReference(null, "Metformine", "Metformine", "A10BA02"), // Diabète de type 2
                new DrugReference(null, "Diamicron", "Gliclazide", "A10BB09"), // Diabète de type 2
                new DrugReference(null, "Lantus", "Insuline glargine", "A10AE04"), // Diabète de type 1 et 2 (insuline lente)

                // 🔹 Cholestérol (facteur de risque chronique)
                new DrugReference(null, "Tahor", "Atorvastatine", "C10AA05"), // Hypercholestérolémie
                new DrugReference(null, "Crestor", "Rosuvastatine", "C10AA07") // Hypercholestérolémie
        ));
    }

}
