# Biometric authentification

Programme ayant pour but, dans un premier temps, de détecter les ellipses présents sur des images synthétiques (cf. samples/synthetic) puis sur des images réelles où figureront des visages (cf. samples/real). Dans un second temps, le programme sera étendu à la vidéo avec une détection en temps réelle sur une image plus ou moins bruitée (selon la qualité de la caméra). Enfin, le but final du programme est de détecter les iris des yeux d'un visage.

## Spécifications techniques
Le programme est écrit en C++ et se sert de la bibliothèque OpenCV pour l'aquisition de l'image. Entre autres, il utilise divers procédés de traitement d'images telles que la détection de contours par calcul de gradient ainsi que la transformée de Hough généralisée appliquées aux ellipses.

## Attendus
* Gradient ;
* Image d'accumulation ;
* Centre de gravité ;
* Ellipses détectées.

## Usage
Installation :
```console
cd <PROJECT_FOLDER>
make
make clean
```

Sur une image (option -f ou --file) :
```console
bin/BiometricAuth -f <FILE_PATH>
```

En temps réel (sans option) :
```console
bin/BiometricAuth 
```