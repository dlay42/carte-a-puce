#include <opencv2/highgui.hpp>
#include <iostream>

#include <map>
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
    cv::Mat result_image = cv::Mat(cv::Size(original_image.cols, original_image.rows), CV_32F);

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

            result_image.at<float>(y,x) = avg_I; 
        } 
    }

    return result_image;
}

cv::Mat sobel_filter_y(cv::Mat original_image) {

    double avg_I = 0;
    cv::Mat result_image = cv::Mat(cv::Size(original_image.cols, original_image.rows), CV_32F);

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
            result_image.at<float>(y,x) = avg_I; 
        } 
    }

    return result_image;
}

cv::Mat compute_gradient_norm(cv::Mat sobel_x, cv::Mat sobel_y, int ceil) {

    cv::Mat result_image = cv::Mat(cv::Size(sobel_x.cols, sobel_x.rows), CV_8UC3, cv::Scalar(0,0,0));

    for (int y = 0; y < sobel_x.rows; y++) {
        for (int x = 0; x < sobel_x.cols; x++) {
            int color = (int) sqrt(sobel_x.at<float>(y,x) * sobel_x.at<float>(y,x) + sobel_y.at<float>(y,x) * sobel_y.at<float>(y,x));

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
            // Control overflow
            if (
                x == center[0] || y == center[1] &&
                y >= 0 && y <= image.rows &&
                x >= 0 && x <= image.cols
            ) {
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

void draw_shape(cv::Mat image, std::vector<cv::Vec2i> shape, cv::Vec2i center) {
    for (std::vector<cv::Vec2i>::const_iterator it = shape.begin(); it != shape.end(); ++it) {
        if (
            center[1] + (*it)[1] >= 0 && center[1] + (*it)[1] <= image.rows &&
            center[0] + (*it)[0] >= 0 && center[0] + (*it)[0] <= image.cols
        ) {
            image.at<cv::Vec3b>(center[1] + (*it)[1], center[0] + (*it)[0])[0] = 0;
            image.at<cv::Vec3b>(center[1] + (*it)[1], center[0] + (*it)[0])[1] = 0;
            image.at<cv::Vec3b>(center[1] + (*it)[1], center[0] + (*it)[0])[2] = 255;
        }
    }
}


int main( int argc, char** argv ) {

    std::string image_path;
    std::string shape_path;

    // Command line arguments parser
    // (Inspiré de l'exemple provenant de https://www.gnu.org)
    int c;
    
    while (1) {
        static struct option long_options[] = {
            {"file",       optional_argument, 0, 'f'},
            {"shape",      required_argument, 0, 's'}
        };

        int option_index = 0;
        c = getopt_long (argc, argv, "f:s:", long_options, &option_index);

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

            // Shape file path --file
            case 's':
                try {
                    shape_path = optarg;
                } catch (...) {
                    std::cerr << argv[0] << ": file path (option --shape ou -s) invalide: chaîne de caractères attendue -- " << optarg << " reçu." << std::endl;
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

        std::vector<cv::Vec2i> shape;
        std::vector<cv::Vec2i> ellipse_lut[361] = {std::vector<cv::Vec2i>(0)};


        ////////////////////////////////////
        // I - Train model
        ////////////////////////////////////

        // 0 - Acquisition de l'image
        image = cv::imread(shape_path);
        
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
        gradient_image = compute_gradient_norm(sobel_x_image, sobel_y_image, 75);

        // 2 - LUT
        // 2.a - Centre de gravité
        cv::Vec2i center(compute_center(gradient_image));
        draw_cross(gradient_image, 10, center);

        int theta = 0;
        double beta = 0;

        for (int y = 0; y < gradient_image.rows; y++) {
            for (int x = 0; x < gradient_image.cols; x++) {
                if (gradient_image.at<cv::Vec3b>(y,x)[0] > 0) {
                    //std::cout <<sobel_y_image.at<float>(y,x) << " x " << sobel_x_image.at<float>(y,x) << std::endl;
                    theta = floor(180 + atan2(sobel_y_image.at<float>(y,x) - 255/2, sobel_x_image.at<float>(y,x) - 255/2) * (180/M_PI));
                    //beta = atan2(center[1] - y, center[0] - x);
                    ellipse_lut[theta].push_back(cv::Vec2i(center[0] - x, center[1] - y));
                    //ellipse_lut[theta] = beta;
                }
            }
        }

        // Save shape
        for (int y = 0; y < gradient_image.rows; y++) {
            for (int x = 0; x < gradient_image.cols; x++) {
                if (gradient_image.at<cv::Vec3b>(y,x)[0] > 0) {
                    shape.push_back(cv::Vec2i(x - center[0], y - center[1]));
                }
            }
        }


        ////////////////////////////////////
        // II - Detect shapes
        ////////////////////////////////////

        image = cv::imread(image_path);

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
        gradient_image = compute_gradient_norm(sobel_x_image, sobel_y_image, 40);

        // Nombre de points contours (pour normalisation)
        double normalized_accumulator = 255 / get_edges_points_number(gradient_image);
        cv::Mat accumulation_mat = cv::Mat(cv::Size(image.cols, image.rows), CV_32F);

        // 2 - Matrice d'accumulation
        for (int y = 0; y < gradient_image.rows; y++) {
            for (int x = 0; x < gradient_image.cols; x++) {
                if (gradient_image.at<cv::Vec3b>(y,x)[0] > 0) {

                    theta = floor(180 + atan2(sobel_y_image.at<float>(y,x) - 255/2, sobel_x_image.at<float>(y,x) - 255/2) * (180/M_PI));

                    for (std::vector<cv::Vec2i>::const_iterator it = ellipse_lut[theta].begin(); it != ellipse_lut[theta].end(); ++it) {
                        if (y + (*it)[1] >= 0 && y + (*it)[1] <= image.rows && x + (*it)[0] >= 0 && x + (*it)[0] <= image.cols) {
                            accumulation_mat.at<float>(y + (*it)[1], x + (*it)[0]) += normalized_accumulator;
                        }
                    }

                }
            }
        }

        // 3 - Détection des formes
        float max = 0;
        cv::Vec2i max_point(0, 0);
        for (int y = 0; y < accumulation_mat.rows; y++) {
            for (int x = 0; x < accumulation_mat.cols; x++) {
                if (accumulation_mat.at<float>(y,x) >= max) {
                    max = accumulation_mat.at<float>(y,x);
                    max_point = cv::Vec2i(x, y);
                }
            }
        }

        detected_circles = cv::Mat(cv::Size(image.cols, image.rows), CV_8UC3, cv::Scalar(0,0,0));
        float mult_normalizer = 255/max;
        for (int y = 0; y < gradient_image.rows; y++) {
            for (int x = 0; x < gradient_image.cols; x++) {
                detected_circles.at<cv::Vec3b>(y,x)[0] = floor(accumulation_mat.at<float>(y,x)*mult_normalizer);
                detected_circles.at<cv::Vec3b>(y,x)[1] = floor(accumulation_mat.at<float>(y,x)*mult_normalizer);
                detected_circles.at<cv::Vec3b>(y,x)[2] = floor(accumulation_mat.at<float>(y,x)*mult_normalizer);
            }
        }

        for (int y = 0; y < detected_circles.rows; y++) {
            for (int x = 0; x < detected_circles.cols; x++) {
                if (detected_circles.at<cv::Vec3b>(y,x)[0] > 250) {
                    draw_shape(detected_circles, shape, cv::Vec2i(x,y));
                    draw_cross(detected_circles, 10, cv::Vec2i(x,y));
            
                    draw_shape(image, shape, cv::Vec2i(x,y));
                    draw_cross(image, 10, cv::Vec2i(x,y));
                }
            }
        }


        ////////////////////////////////////
        // III - Visualisation / Result
        ////////////////////////////////////

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
        cv::Mat frame;
        //--- INITIALIZE VIDEOCAPTURE
        cv::VideoCapture cap;
        // open the default camera using default API
        // cap.open(0);
        // OR advance usage: select any API backend
        int deviceID = 0;             // 0 = open default camera
        int apiID = cv::CAP_ANY;      // 0 = autodetect default API
        // open selected camera using selected API
        cap.open(deviceID, apiID);
        // check if we succeeded
        if (!cap.isOpened()) {
            std::cerr << "ERROR! Unable to open camera\n";
            return -1;
        }
        //--- GRAB AND WRITE LOOP
        std::cout << "Start grabbing" << std::endl
            << "Press any key to terminate" << std::endl;
        for (;;)
        {
            // wait for a new frame from camera and store it into 'frame'
            cap.read(frame);
            // check if we succeeded
            if (frame.empty()) {
                std::cerr << "ERROR! blank frame grabbed\n";
                break;
            }
            // show live and wait for a key with timeout long enough to show images
            imshow("Live", frame);
            if (cv::waitKey(5) >= 0)
                break;
        }
    }

    return 0;
}