#include <opencv2/highgui.hpp>
#include <iostream>

#include <getopt.h>
#include <cmath> 

double vector_avg(cv::Vec3b vector) {
    return (vector[0] + vector[1] + vector[2]) /3;
}

cv::Mat grayscale_image(cv::Mat original_image) {

    cv::Mat result_image = cv::Mat(cv::Size(original_image.cols, original_image.rows), CV_8UC3, cv::Scalar(0,0,0));

    for (int y = 0; y < original_image.rows; y++) { 
        for (int x = 0; x < original_image.cols; x++) { 
            double rgb_avg = vector_avg(original_image.at<cv::Vec3b>(y,x)); 
            result_image.at<cv::Vec3b>(y,x)[0] = rgb_avg;
            result_image.at<cv::Vec3b>(y,x)[1] = rgb_avg;
            result_image.at<cv::Vec3b>(y,x)[2] = rgb_avg;
        }
    }

    return result_image;
}

cv::Mat sobel_filter_x(cv::Mat original_image) {

    double avg_I = 0;
    cv::Mat result_image = cv::Mat(cv::Size(original_image.cols, original_image.rows), CV_8UC3, cv::Scalar(0,0,0));

    for (int y = 0; y < original_image.rows; y++) { 
        for (int x = 0; x < original_image.cols; x++) { 
			avg_I = 0;
			/* LAPLACIEN (masque) :
			 * -1 0 +1
			 * -2 0 +2
			 * -1 0 +1
			 */
			for (int h = -1; h <= 1; h++) {
				for (int l = -1; l <= 1; l++) {
					if (l != 0) {
						avg_I += l*original_image.at<cv::Vec3b>(y+h, x+l)[0];
						if (h == 0)
							avg_I += l*original_image.at<cv::Vec3b>(y+h, x+l)[0];
					}
				}
			}
            if (avg_I < 0)
                avg_I *= -1;
            avg_I = floor(avg_I/4);

            result_image.at<cv::Vec3b>(y,x)[0] = avg_I; 
            result_image.at<cv::Vec3b>(y,x)[1] = avg_I; 
            result_image.at<cv::Vec3b>(y,x)[2] = avg_I; 
        } 
    }

    return result_image;
}

cv::Mat sobel_filter_y(cv::Mat original_image) {

    double avg_I = 0;
    cv::Mat result_image = cv::Mat(cv::Size(original_image.cols, original_image.rows), CV_8UC3, cv::Scalar(0,0,0));

    for (int y = 0; y < original_image.rows; y++) { 
        for (int x = 0; x < original_image.cols; x++) { 
			avg_I = 0;
			/* LAPLACIEN (masque) :
			 * -1 -2 -1
			 *  0  0  0
			 *  1  2  1
			 */
			for (int h = -1; h <= 1; h++) {
				for (int l = -1; l <= 1; l++) {
					if (h != 0) {
						avg_I += h*original_image.at<cv::Vec3b>(y+h, x+l)[0];
						if (l == 0)
							avg_I += h*original_image.at<cv::Vec3b>(y+h, x+l)[0];
					}
				}
			}

            if (avg_I < 0)
                avg_I *= -1;
            avg_I = floor(avg_I/4);
            result_image.at<cv::Vec3b>(y,x)[0] = avg_I; 
            result_image.at<cv::Vec3b>(y,x)[1] = avg_I; 
            result_image.at<cv::Vec3b>(y,x)[2] = avg_I; 
        } 
    }

    return result_image;
}

cv::Mat compute_gradient_norm(cv::Mat sobel_x, cv::Mat sobel_y, int ceil) {

    cv::Mat result_image = cv::Mat(cv::Size(sobel_x.cols, sobel_x.rows), CV_8UC3, cv::Scalar(0,0,0));

    for (int y = 0; y < sobel_x.rows; y++) {
        for (int x = 0; x < sobel_x.cols; x++) {
            int color = (int) sqrt(sobel_x.at<cv::Vec3b>(y,x)[0] * sobel_x.at<cv::Vec3b>(y,x)[0] + sobel_y.at<cv::Vec3b>(y,x)[0] * sobel_y.at<cv::Vec3b>(y,x)[0]);

            if (color < ceil) {
                result_image.at<cv::Vec3b>(y,x)[0] = 0;
                result_image.at<cv::Vec3b>(y,x)[1] = 0;
                result_image.at<cv::Vec3b>(y,x)[2] = 0;
            } else {
                result_image.at<cv::Vec3b>(y,x)[0] = 255;
                result_image.at<cv::Vec3b>(y,x)[1] = 255;
                result_image.at<cv::Vec3b>(y,x)[2] = 255;
            }
        }
    }

	return result_image;
}

cv::Vec2i compute_center(cv::Mat image) {
    int Cx = 0, Cy = 0, points_number = 0;

    for (int y = 0; y < image.rows; y++) {
        for (int x = 0; x < image.cols; x++) {
            if (image.at<cv::Vec3b>(y,x)[0] > 0) {
                Cx += x;
                Cy += y;
                points_number += 1;
            }
        }
    }

    cv::Vec2i center(Cx/points_number, Cy/points_number);
    return center;

}

void draw_cross(cv::Mat image, int radius, cv::Vec2i center) {
    for (int y = center[1]-radius; y < center[1]+radius; y++) {
        for (int x = center[0]-radius; x < center[0]+radius; x++) {
            if (x == center[0] || y == center[1]) {
                image.at<cv::Vec3b>(y,x)[0] = 0;
                image.at<cv::Vec3b>(y,x)[1] = 0;
                image.at<cv::Vec3b>(y,x)[2] = 255;
            }
        }
    }
}

double get_edges_points_number(cv::Mat image) {
    int counter = 0;

    for (int y = 0; y < image.rows; y++) {
        for (int x = 0; x < image.cols; x++) {
            if (image.at<cv::Vec3b>(y,x)[0] > 0) {
                counter++;
            }
        }
    }

    return counter;
}

int main( int argc, char** argv ) {

    std::string image_path;

    // Command line arguments parser
    // (Inspiré de l'exemple provenant de https://www.gnu.org)
    int c;
    
    while (1) {
        static struct option long_options[] = {{"file",       optional_argument, 0, 'f'}};

        int option_index = 0;
        c = getopt_long (argc, argv, "f:", long_options, &option_index);

        // Détection de la fin des options
        if (c == -1)
            break;

        switch (c) {
            // File path --file
            case 'f':
                try {
                    image_path = optarg;
                } catch (...) {
                    std::cerr << argv[0] << ": file path (option --file ou -f) invalide: chaîne de caractères attendue -- " << optarg << " reçu." << std::endl;
                    throw;
                }
                break;

            case '?':
                // Message d'erreur généré par getopt_long
                return 2;
                break;

            default:
                abort ();
        }
    }

    // CAS #1 : Fichier (image) spécifiée : application des procédés des traitements
    //          d'image sur l'image en question.
    if (!image_path.empty()) {

        cv::Mat image;
        cv::Mat gradient_image;
        cv::Mat accumulation_matrice;
        cv::Mat detected_circles;

        // 0 - Acquisition de l'image
        image = cv::imread(image_path);
        
        if(!image.data) {
            std::cerr <<  "Image has not been loaded properly" << std::endl ;
            return 2;
        }

        // 0bis- Mettre l'image en niveau de gris
        cv::Mat grayscaled_image = grayscale_image(image);

        // 1 - Détection de contours : filtre + gradient
        // 1.a - Filtre - gradient
        cv::Mat sobel_x_image = sobel_filter_x(grayscaled_image);
        cv::Mat sobel_y_image = sobel_filter_y(grayscaled_image);

        // 1.b - Norme
        gradient_image = compute_gradient_norm(sobel_x_image, sobel_y_image, 50);

        // 2 - LUT
        // 2.a - Centre de gravité
        cv::Vec2i center(compute_center(gradient_image));
        draw_cross(gradient_image, 10, center);

        int theta = 0;
        int beta = 0;
        int ellipse_lut[360];

        for (int y = 0; y < gradient_image.rows; y++) {
            for (int x = 0; x < gradient_image.cols; x++) {
                if (gradient_image.at<cv::Vec3b>(y,x)[0] > 0) {
                    theta = floor(atan2(sobel_y_image.at<cv::Vec3b>(y,x)[0], sobel_x_image.at<cv::Vec3b>(y,x)[0]) * (180/M_PI));
                    beta = floor(atan2(center[1] - y, center[0] - x) * (180/M_PI));
                    ellipse_lut[theta] = beta;

                    //std::cout << x << " ; " << y <<" : " << theta << " --- " << beta << std::endl;
                }
            }
        }

// -------------------------------- TEST


        image = cv::imread("samples/synthetic/azerty.png");
        
        if(!image.data) {
            std::cerr <<  "Image has not been loaded properly" << std::endl ;
            return 2;
        }

        // 0bis- Mettre l'image en niveau de gris
        grayscaled_image = grayscale_image(image);

        // 1 - Détection de contours : filtre + gradient
        // 1.a - Filtre - gradient
        sobel_x_image = sobel_filter_x(grayscaled_image);
        sobel_y_image = sobel_filter_y(grayscaled_image);

        // 1.b - Norme
        gradient_image = compute_gradient_norm(sobel_x_image, sobel_y_image, 50);

        // Nombre de points contours (pour normalisation)
        double normalized_accumulator = 255 / get_edges_points_number(gradient_image);
        double a = 0, b = 0;

        accumulation_matrice = cv::Mat(cv::Size(image.cols, image.rows), CV_8UC3, cv::Scalar(0,0,0));
        for (int y = 0; y < gradient_image.rows; y++) {
            for (int x = 0; x < gradient_image.cols; x++) {
                if (gradient_image.at<cv::Vec3b>(y,x)[0] > 0) {
                    theta = floor(atan2(sobel_y_image.at<cv::Vec3b>(y,x)[0], sobel_x_image.at<cv::Vec3b>(y,x)[0]) * (180/M_PI));
                    beta = ellipse_lut[theta];
                    a = tan(beta * (M_PI/180));
                    b = y - a * x;
                    
                    std::cout << "y = " << a << " * x + " << b << std::endl;

                    /*
                    for (int seg_y = 0; seg_y < gradient_image.rows; seg_y++) {
                        for (int seg_x = 0; seg_x < gradient_image.cols; seg_x++) {
                            if (y == a * x + b) {
                                accumulation_matrice.at<cv::Vec3b>(y,x)[0] += 1;
                                accumulation_matrice.at<cv::Vec3b>(y,x)[1] += 1;
                                accumulation_matrice.at<cv::Vec3b>(y,x)[2] += 1;
                            }
                        }
                    }
                    */
                }
            }
        }











// ------------------------------------------








        /*
        std::cout << normalized_accumulator << std::endl;


        cv::Mat test = cv::Mat(cv::Size(image.cols, image.rows), CV_32FC1);

        for (int y = 0; y < gradient_image.rows; y++) {
            for (int x = 0; x < gradient_image.cols; x++) {
                if (gradient_image.at<cv::Vec3b>(y,x)[0] > 0) {
                    
                    test.at<cv::Vec3f>(y,x)[0] += normalized_accumulator;
                    test.at<cv::Vec3f>(y,x)[1] += normalized_accumulator;
                    test.at<cv::Vec3f>(y,x)[2] += normalized_accumulator;
                    
                }
            }
        }
        std::cout << test << std::endl;
        cv::namedWindow( "test", cv::WINDOW_AUTOSIZE);
        cv::imshow( "test", test);
        */

        // 3 - Détection d'ellipses (Transformée de Hough généralisée)

        // Visualisation/Résultat
        cv::namedWindow( "Original image", cv::WINDOW_AUTOSIZE);
        cv::imshow( "Original image", image);

        if (image.data) {
            cv::namedWindow( "Filtre & Gradient", cv::WINDOW_AUTOSIZE);
            cv::imshow( "Filtre & Gradient", gradient_image);
        }

        if (accumulation_matrice.data) {
            cv::namedWindow( "Matrice d'accumulation", cv::WINDOW_AUTOSIZE);
            cv::imshow( "Matrice d'accumulation", accumulation_matrice);
        }

        if (detected_circles.data) {
            cv::namedWindow( "Transformée de Hough généralisée", cv::WINDOW_AUTOSIZE);
            cv::imshow( "Transformée de Hough généralisée", detected_circles);
        }

        cv::waitKey(0);
    } else {
    // CAS #2 : Fichier (image) non spécifiée : application des procédés des traite-
    //          -ments d'image sur le périphérique vidéo (temps réel).
        std::cout << "EMPTY" << std::endl;
    }

    return 0;
}