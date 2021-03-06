package cn.edu.ruc.iir.mc.bbm;

import cn.edu.ruc.iir.mc.bbm.domain.Point;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

/**
 * Created by Tao on 2017/4/13.
 */
public class PointUtil {

    public static void main(String[] args) {
        SparkSession spark = SparkSession
                .builder()
                .appName("BBM-Demo")
                .config("spark.some.config.option", "some-value")
                .getOrCreate();
        JavaRDD<Point> point = spark.read().option("header", "true").textFile("/rainbow-manage/Points.txt").javaRDD().map(new Function<String, Point>() {
            public Point call(String line) throws Exception {
                String[] fields = line.split(",");
                Point p = new Point(Integer.valueOf(fields[0]), Float.valueOf(fields[1]), Float.valueOf(fields[2]));
                return p;
            }
        });
        Dataset<Row> dataset = spark.createDataFrame(point, Point.class);
        dataset.createOrReplaceTempView("Point");
        try {
            Dataset<Row> pointDF = spark.sql("select a.* from\n" +
                    "(  select p1.ID, p1.PointX, p1.PointY, p2.ID as P2_ID, p2.PointX as P1_PointX, p2.PointY as P2_PointY, round((cast(p1.PointX as float) - cast(p2.PointX as float)), 4) * round((cast(p1.PointX as float) - cast(p2.PointX as float)), 4) as num from Point p1, Point p2) a where a.num = 4 and a.PointY = 2");
            pointDF.show();
        } catch (Exception e) {
            System.out.print("Error Info: " + e.getMessage());
        }
    }
}
