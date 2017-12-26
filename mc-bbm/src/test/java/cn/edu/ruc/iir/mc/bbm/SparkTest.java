package cn.edu.ruc.iir.mc.bbm;

import cn.edu.ruc.iir.mc.bbm.domain.Weather;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.testng.annotations.Test;

/**
 * @version V1.0
 * @Package: cn.edu.ruc.iir.mc.bbm
 * @ClassName: SparkTest
 * @Description: -Dspark.master=local
 * @author: taoyouxian
 * @date: Create in 2017-11-12 10:51
 **/
public class SparkTest {

    @Test
    public void WeatherCsvTest() {
        SparkSession spark = SparkSession
                .builder()
                .master("spark://10.77.40.236:7077")
                .appName("BBM-Demo")
                .config("spark.executor.memory", "4g")
                .config("spark.driver.memory", "2g")
                .config("spark.executor.cores", "4")
                .config("spark.sql.warehouse.dir", "hdfs://10.77.40.236:9000/spark-warehouse")
                .config("spark.storage.memoryFraction", "0")
                .getOrCreate();
//        Dataset<Row> dataset = spark.read().option("header", "true").csv("/rainbow-manage/weather.csv");
        Dataset<Row> dataset = spark.read().option("header", "true").csv("/rainbow-manage/weatherss.csv");
        dataset.createOrReplaceTempView("weather");
        try {
            Dataset<Row> weatherDF = spark.sql("SELECT location, month, avg(temperature) as temp FROM weather where location = 'BRBRGTWN' GROUP BY location, month ORDER BY month");
            weatherDF.show();
        } catch (Exception e) {
            System.out.print("Error Info: " + e.getMessage());
        }
    }

    @Test
    public void WeatherTextTest() {
        SparkSession spark = SparkSession
                .builder()
//                .master("spark://10.77.40.236:7077")
                .appName("BBM-1M")
                .getOrCreate();
        JavaRDD<Weather> weatherJavaRDD = spark.read().textFile("/rainbow-manage/weathers.csv").javaRDD().map(new Function<String, Weather>() {
            public Weather call(String line) throws Exception {
                String[] fields = line.split(",");
                Weather w = new Weather(fields[0], Integer.valueOf(fields[1]), Integer.valueOf(fields[2]), Integer.valueOf(fields[3]), Float.valueOf(fields[4]));
                return w;
            }
        });
        Dataset<Row> dataset = spark.createDataFrame(weatherJavaRDD, Weather.class);
        dataset.createOrReplaceTempView("Weather");
        try {
            Dataset<Row> pointDF = spark.sql("SELECT location, month, avg(temperature) as temp FROM weather where location = 'ABTIRANA' GROUP BY location, month ORDER BY month");
            pointDF.show();
        } catch (Exception e) {
            System.out.print("Error Info: " + e.getMessage());
        }
    }

    @Test
    public void PointTest() {
    }
}
