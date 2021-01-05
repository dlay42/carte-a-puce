/*
#ifndef BIOMETRICIMAGE_H_
#define BIOMETRICIMAGE_H_

#include <opencv2/highgui.hpp>
#include <iostream>

class BiometricImage {
    protected:
        Mat dfg;

    public:
        /// @brief Constructeur de la classe Simulation
        BiometricImage(double duree_prevue, double temps_moyen_arrivee, int nb_caissiers, double temps_moyen_service);

        // Getter
        double dureePrevue();

        /// @brief Paramètre statistique de sortie du programme.
        /// @return double : durée réelle de la Simulation (dernier Client servi).
        double dureeReelle();

        /// @brief Paramètre statistique de sortie du programme.
        /// @return int : nombre total de Client servi durant la Simulation.
        int mNbClients();
        double tempsMoyenArrivee();
        Banque* mBanque();

        // Setter
        void setHeureActuelle(double heure_actuelle);
        void incrementNbClients();

        /// @brief Algorithme implémentant la Simulation.
        /// @see Arrivee::traiter()
        /// @see Depart::traiter()
        /// @see FileAttente:ajouterContributionLongueurMoyenne(double contribution)
        void lancer();
};

#endif
*/