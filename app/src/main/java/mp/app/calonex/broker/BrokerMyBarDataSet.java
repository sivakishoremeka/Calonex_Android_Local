package mp.app.calonex.broker;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.List;

import static mp.app.calonex.landlord.dashboard.BarChartFragment.MAxValue;
import static mp.app.calonex.landlord.dashboard.BarChartFragment.MinValue;

public class BrokerMyBarDataSet extends BarDataSet {

        int dataSize;
    public BrokerMyBarDataSet(List<BarEntry> yVals, String label, int size) {
        super(yVals, label);
    }

    @Override
    public int getColor(int index) {
        if(getEntryForIndex(index).getY() >= MAxValue) // less than 95 green
            return mColors.get(0);
        else if(getEntryForIndex(index).getY() <= MinValue) // less than 100 orange
            return mColors.get(1);
        else // greater or equal than 100 red
            return mColors.get(2);
    }

}