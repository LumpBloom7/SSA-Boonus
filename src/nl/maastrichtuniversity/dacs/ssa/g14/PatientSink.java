
package nl.maastrichtuniversity.dacs.ssa.g14;

import simulation.Acceptor;

import java.util.ArrayList;
/**
 *	A sink
 *	@author Joel Karel
 *	@version %I%, %G%
 */
public class PatientSink implements Acceptor<Patient>
{
    /** All products are kept */
    private ArrayList<Patient> products;
    /** All properties of products are kept */
    private ArrayList<Integer> numbers;
    private ArrayList<Double> times;
    private ArrayList<String> events;
    private ArrayList<String> stations;
    /** Counter to number products */
    private int number;
    /** Name of the sink */
    private String name;

    /**
     *	Constructor, creates objects
     */
    public PatientSink(String n)
    {
        name = n;
        products = new ArrayList<>();
        numbers = new ArrayList<>();
        times = new ArrayList<>();
        events = new ArrayList<>();
        stations = new ArrayList<>();
        number = 0;
    }

    @Override
    public boolean giveProduct(Patient p)
    {
        number++;
        products.add(p);
        // store stamps
        ArrayList<Double> t = p.getTimes();
        ArrayList<String> e = p.getEvents();
        ArrayList<String> s = p.getStations();
        for(int i=0;i<t.size();i++)
        {
            numbers.add(number);
            times.add(t.get(i));
            events.add(e.get(i));
            stations.add(s.get(i));
        }
        return true;
    }

    public int[] getNumbers()
    {
        numbers.trimToSize();
        int[] tmp = new int[numbers.size()];
        for (int i=0; i < numbers.size(); i++)
        {
            tmp[i] = (numbers.get(i)).intValue();
        }
        return tmp;
    }

    public double[] getTimes()
    {
        times.trimToSize();
        double[] tmp = new double[times.size()];
        for (int i=0; i < times.size(); i++)
        {
            tmp[i] = (times.get(i)).doubleValue();
        }
        return tmp;
    }

    public String[] getEvents()
    {
        String[] tmp = new String[events.size()];
        tmp = events.toArray(tmp);
        return tmp;
    }

    public String[] getStations()
    {
        String[] tmp = new String[stations.size()];
        tmp = stations.toArray(tmp);
        return tmp;
    }
}