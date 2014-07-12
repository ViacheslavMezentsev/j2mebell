
// <editor-fold defaultstate="collapsed" desc=" Подключаемые модули ">

import java.util.Timer;

// </editor-fold>

public class Schedule {

    // <editor-fold defaultstate="collapsed" desc=" Поля класса ">

    public int Тип;
    public int Длительность;
    public long ВремяСтарта;
    public String Путь;
    public Timer Таймер;

    // </editor-fold>

    // <editor-fold desc=" Конструктор ">

    Schedule( long ВремяСтарта, int Длительность, String Путь ) {

        this.ВремяСтарта = ВремяСтарта;
        this.Путь = Путь;
        this.Длительность = Длительность;
    }

    // </editor-fold>
    
}