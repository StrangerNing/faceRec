package com.welcome;

import com.welcome.faceapi.FaceApi;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.concurrent.FutureTask;

import static org.opencv.core.Core.getTickCount;
import static org.opencv.core.Core.getTickFrequency;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.*;
import static org.opencv.imgproc.Imgproc.equalizeHist;
import static org.opencv.objdetect.Objdetect.CASCADE_DO_ROUGH_SEARCH;
import static org.opencv.objdetect.Objdetect.CASCADE_FIND_BIGGEST_OBJECT;


/**
 * @author Tsening Chu
 * @version 1.0
 * @date 2018/11/30
 */
public class Welcome extends JPanel {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private BufferedImage mImg;
    private Boolean isPerson = false;
    private Boolean close = true;
    private UserInfo userInfo = new UserInfo();

    private BufferedImage mat2BI(Mat mat) {
        int dataSize = mat.cols() * mat.rows() * (int) mat.elemSize();
        byte[] data = new byte[dataSize];
        mat.get(0, 0, data);
        int type = mat.channels() == 1 ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_3BYTE_BGR;

        if (type == BufferedImage.TYPE_3BYTE_BGR) {
            for (int i = 0; i < dataSize; i += 3) {
                byte blue = data[i];
                data[i] = data[i + 2];
                data[i + 2] = blue;
            }
        }
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        image.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data);
        return image;
    }

    @Override
    public void paintComponent(Graphics g) {
        if (mImg != null) {
            g.drawImage(mImg, 0, 0, mImg.getWidth(), mImg.getHeight(), (ImageObserver) this);
        }
    }

    public Mat detect(Mat img, CascadeClassifier cascade, double scale) {
        try {
            double t = 0;
            MatOfRect faces = new MatOfRect();
            Mat gray = new Mat();
            Mat smallImg = new Mat();
            float searchScaleFactor = 1.2f;
            int minNeighbors = 3;
            //只检测脸最大的人
            int flags = CASCADE_FIND_BIGGEST_OBJECT | CASCADE_DO_ROUGH_SEARCH;
            //检测多个人
            //int flags = CASCADE_SCALE_IMAGE;
            Size minFeatureSize = new Size(30, 30);
            Size maxFeatureSize = new Size(1000, 1000);
            double fx = 1 / scale;

            //将原图像转为灰度图
            cvtColor(img, gray, COLOR_BGR2GRAY);
            //缩放图像
            Imgproc.resize(gray, smallImg, new Size(), fx, fx, INTER_LINEAR_EXACT);
            //直方图均衡化，提高图像质量
            equalizeHist(smallImg, smallImg);

            //检测目标
            t = (double) getTickCount();
            cascade.detectMultiScale(smallImg, faces, searchScaleFactor, minNeighbors, flags, minFeatureSize, maxFeatureSize);
            t = (double) getTickCount() - t;
            //System.out.println("detect time = " + (t * 1000 / getTickFrequency()) + "ms");

            Rect[] rects = faces.toArray();
            if (rects != null && rects.length > 0) {
                for (Rect rect : rects) {
                    Imgproc.rectangle(img, new Point(rect.x * scale, rect.y * scale), new Point((rect.x + rect.width) * scale, (rect.y + rect.height) * scale),
                            new Scalar(0, 255, 0), 2);
                }
                if (isPerson) {
                    imwrite("face.jpg", img);
                    System.out.println("人脸已保存");
                    FutureTask<UserInfo> futureTask = new FutureTask<>(new FaceRecognition());
                    new Thread(futureTask).start();
                    this.userInfo = futureTask.get();
                    isPerson = false;
                }
            } else {
                isPerson = true;
            }
            //System.out.println("人脸数量："+rects.length);
        }catch (Exception e){
            System.out.println("error:"+e);
        }

        return img;
    }

    public static void main(String[] args) {
        try {
            if (!FaceApi.getAuth()) {
                throw new RuntimeException("获取accessToken失败");
            }
            //FaceApi.groupDelete("FaceWelcome");
            FaceApi.groupAdd("FaceWelcome");
            Welcome panel = new Welcome();
            Mat capImg = new Mat();
            VideoCapture capture = new VideoCapture(0);
            int height = (int) capture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
            int width = (int) capture.get(Videoio.CAP_PROP_FRAME_WIDTH);
            if (height == 0 && width == 0) {
                throw new Exception("摄像头打开失败");
            }
            JFrame frame = new JFrame("Welcome");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setContentPane(panel);
            frame.setVisible(true);
            frame.setSize(width + frame.getInsets().left + frame.getInsets().right, height + frame.getInsets().top + frame.getInsets().bottom);

            JLabel label = new JLabel();
            label.setFont(new Font("仿宋",Font.BOLD,30));
            panel.setLayout(null);
            label.setBounds(5,10,500, 50 );
            panel.add(label);

            Mat temp = new Mat();

            CascadeClassifier faceCascade = new CascadeClassifier();
            double scale = 4;

            /*加载分类器*/
            Boolean nRet = faceCascade.load("haarcascades/haarcascade_frontalface_default.xml");
            if (!nRet) {
                System.out.println("加载分类器失败");
            }

            while (panel.close) {
                if (panel.userInfo.getUserId()!=null) {
                    label.setText("欢迎，" + panel.userInfo.getUserId() + " 第" + panel.userInfo.getVisitedTimes() + "次光临");
                }
                capture.read(capImg);
                Imgproc.cvtColor(capImg, temp, Imgproc.COLOR_RGB2GRAY);
                panel.mImg = panel.mat2BI(panel.detect(capImg, faceCascade, scale));
                panel.repaint();
            }
            capture.release();
            frame.dispose();
        } catch (Exception e) {
            System.out.println("ERROR:" + e);
        }
    }


}
