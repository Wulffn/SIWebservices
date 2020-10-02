package dk.mwnck.springsoap.gen;

import java.util.List;

public class DutyCalculator
{
    public static double calculate(List<Car> taxatedCars, List<Car> cars)
    {
        return getAvgPrice(taxatedCars) - getAvgPrice(cars);
    }

    private static double getAvgPrice(List<Car> cars)
    {
        return cars.stream().mapToDouble(Car::getPrice).sum()/cars.size();
    }
}
