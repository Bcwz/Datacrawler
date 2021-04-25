import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.ApplicationFrame;

import javax.swing.*;

 
public class PieChart_AWT extends ApplicationFrame {

   public PieChart_AWT( String title, int count0, int count1, int count2, int count3, int count4) {
      super( title );
      setContentPane(createDemoPanel(title, count0, count1, count2, count3, count4));
   }
   
   private static PieDataset createDataset(int count0, int count1, int count2, int count3, int count4) {
      DefaultPieDataset dataset = new DefaultPieDataset( );
      dataset.setValue( "Positive" , count4 );
      dataset.setValue( "Positive-Neutral" , count3 );
      dataset.setValue( "Neutral" , count2 );
      dataset.setValue( "Negative-Neutral" , count1 );
      dataset.setValue( "Negative" , count0 );
      return dataset;         
   }
   
   private static JFreeChart createChart( String title ,PieDataset dataset ) {
      JFreeChart chart = ChartFactory.createPieChart(
         title,   // chart title
         dataset,               // data
         true,                  // include legend
         true, 
         false);

      return chart;
   }
   public static JPanel createDemoPanel(String title, int count0, int count1, int count2, int count3, int count4 ) {
      JFreeChart chart = createChart(title, createDataset(count0, count1, count2, count3, count4));
      return new ChartPanel( chart ); 
   }

}