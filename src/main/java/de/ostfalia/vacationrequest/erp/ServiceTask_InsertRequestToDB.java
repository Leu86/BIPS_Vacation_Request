package de.ostfalia.vacationrequest.erp;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import de.ostfalia.vacationrequest.DatabaseConnection;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.ChronoUnit;
import java.time.ZoneId;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.Date;

public class ServiceTask_InsertRequestToDB implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Date dateStart = (Date) execution.getVariable("REQUESTVACATION_START");
        LocalDate requestVacationStart = LocalDate.ofInstant(dateStart.toInstant(), ZoneId.systemDefault()) ;

        Date dateEnd = (Date) execution.getVariable("REQUESTVACATION_END");
        LocalDate requestVacationEnd = LocalDate.ofInstant(dateEnd.toInstant(), ZoneId.systemDefault()) ;;

        long requestVacationDays = ChronoUnit.DAYS.between(requestVacationStart, requestVacationEnd.plusDays(1));
        long requestVacationBusinessDays = countBusinessDaysBetween(requestVacationStart, requestVacationEnd, requestVacationDays);
        long requestVacationHolidays = countHolidaysBetween(requestVacationStart, requestVacationEnd);

        Connection connection = DatabaseConnection.getConnection();
        CallableStatement max = connection.prepareCall("SELECT MAX(idUrlaubsantrag) FROM urlaubsantrag");
        ResultSet resultSet = max.executeQuery();
        int idRequestVacation = !resultSet.next() ? 0 : resultSet.getInt(1) + 1;
        resultSet.close();

        execution.setVariable("REQUESTVACATION_ID", idRequestVacation);
        execution.setVariable("REQUESTVACATION_BUSINESSDAYS", (int) requestVacationBusinessDays - (int) requestVacationHolidays);

        String sql = "INSERT INTO `vacation_request`.`urlaubsantrag` (`idUrlaubsantrag`, `idMitarbeiter`, `Beginn`, `Ende`, `Kalendertage`, `Arbeitstage`, `Wochenendtage`, `Feiertage`) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

        CallableStatement callableStatement = connection.prepareCall(sql);
        callableStatement.setInt(1, (int) execution.getVariable("REQUESTVACATION_ID"));
        callableStatement.setInt(2, (int) execution.getVariable("EMPLOYEE_ID"));
        callableStatement.setObject(3, java.sql.Date.valueOf(requestVacationStart));
        callableStatement.setObject(4, java.sql.Date.valueOf(requestVacationEnd));
        callableStatement.setLong(5, requestVacationDays);
        callableStatement.setInt(6, (int) execution.getVariable("REQUESTVACATION_BUSINESSDAYS"));
        callableStatement.setLong(7, requestVacationDays-requestVacationBusinessDays);
        callableStatement.setLong(8, requestVacationHolidays);
        callableStatement.executeUpdate();

        callableStatement.close();
        connection.close();
    }
    private static long countBusinessDaysBetween(LocalDate startDate, LocalDate endDate, long daysBetween) {
        Predicate<LocalDate> isWeekend = date -> date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
        long businessDays = Stream.iterate(startDate, date -> date.plusDays(1)).limit(daysBetween).filter((isWeekend).negate()).count();

        return businessDays;
    }
    private static long countHolidaysBetween(LocalDate startDate, LocalDate endDate) throws Exception {
        Connection connection = DatabaseConnection.getConnection();
        String query = "SELECT Datum FROM feiertage_nds_2021";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        
        List<LocalDate> listHolidays = new ArrayList<>();
        while(resultSet.next()){
            listHolidays.add(LocalDate.parse(resultSet.getString("Datum")));
        }  

        resultSet.close();
        preparedStatement.close();
        connection.close(); 

        long holidays = 0;
        for(LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
            int i = 0;          
            if(date.isEqual(listHolidays.get(i))) {
                holidays++;
            } i++;
        }
        return holidays;
    }
}

