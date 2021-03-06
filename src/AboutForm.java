
// <editor-fold defaultstate="collapsed" desc=" Подключаемые модули ">

import java.util.*;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

// </editor-fold>

public class AboutForm implements CommandListener {

    private Command КомандаНазад = new Command ( "Назад", Command.BACK, 0 );

    // <editor-fold defaultstate="collapsed" desc=" Поля класса ">

    private Form Форма;
    private MIDlet Мидлет;
    private Display Дисплей;
    private Displayable РодительскоеОкно;
    private Schedules МенеджерРасписаний;
    private Settings Настройки;

    // </editor-fold>

    // <editor-fold desc=" Конструктор ">

    public AboutForm( MIDlet Мидлет, Displayable родитель, Schedules менеджер, Settings Настройки ) {

        this.Мидлет = Мидлет;
        this.Дисплей = Display.getDisplay( Мидлет );
        this.РодительскоеОкно = родитель;
        МенеджерРасписаний = менеджер;
        this.Настройки = Настройки;

        // Окно "О программе"
        Форма = new Form( "О программе" );

        Форма.append( "Название: Школьник-3\n" );
        Форма.append( "Версия: " + Мидлет.getAppProperty( "MIDlet-Version" ) + "\n" );
        Форма.append( "Назначение: Программа предназначена для " +
                "воспроизведения звуковых файлов в соответствии с расписанием. Может " +
                "быть использована для напоминания о подаче школьных звонков или " +
                "в качестве альтернативы школьному звонку при подключении телефона " +
                "к школьному радиоузлу.\n" );

        Форма.append( "Автор: Мезенцев Вячеслав Николаевич\n" );
        Форма.append( "ICQ: 10333578\n" );
        Форма.append( "Skype: viacheslavmezentsev\n" );

        Форма.append( "Сайт: http://школьныйзвонок.рф\n" );
        Форма.append( "Форум: http://школьныйзвонок.рф/forum/\n" );
        Форма.append( "Клуб: http://vkontakte.ru/club18953849\n" );

        Форма.append( "E-mail: viacheslavmezentsev@gmail.com\n" );

        Форма.append( "тел./факс: 8 (343) 217-26-76\n" );

        Форма.append( "[Отладка]\n" );
        Форма.append( "Начало работы: " + НачалоРаботыВТекст() + "\n" );

        long Сейчас = System.currentTimeMillis();
//        long Поправка = Сейчас - МенеджерРасписаний.НачалоРаботы;
//        Поправка /= Scheduler.СЕК_В_СУТКИ * Scheduler.МИЛЛСЕК_В_СЕКУНДЕ;
        long Поправка = Настройки.Основные.КолвоСуток;
        Поправка *= Настройки.Основные.СуточнаяПоправка * Scheduler.МИЛЛСЕК_В_СЕКУНДЕ;
        Поправка += МенеджерРасписаний.ДобавочнаяПоправка * Scheduler.МИЛЛСЕК_В_СЕКУНДЕ;

        // Продолжительность работы программы
        long Интервал = Сейчас - МенеджерРасписаний.НачалоРаботы + Поправка;
        if ( МенеджерРасписаний.ПервыеСутки != true ) {
            Интервал -= МенеджерРасписаний.ДобавочнаяПоправка * Scheduler.МИЛЛСЕК_В_СЕКУНДЕ;
        }
        Форма.append( "Продолжительность: " + ПродолжительностьВТекст( Интервал ) + "\n" );

        Форма.append( "Суточная поправка: " + Настройки.Основные.СуточнаяПоправка + " сек\n" );

        Форма.append( "Добавочная поправка: " + МенеджерРасписаний.ДобавочнаяПоправка + " сек\n" );

        Форма.append( "Текущая поправка: " + ( Поправка / Scheduler.МИЛЛСЕК_В_СЕКУНДЕ ) + " сек\n" );

        Форма.addCommand( КомандаНазад );

        Форма.setCommandListener( this );

        main.Логгер.info( "[AboutForm.java]: AboutForm()" );
    }

    // </editor-fold>

    // <editor-fold desc=" Методы класса ">

    public String НачалоРаботыВТекст() {

        // Записываем значение поправки в лог программы
        Calendar Календарь;
        String Текст;
        String Неделя[] = { "Вс", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб" };
        String ИмяМесяца[] = { "Янв", "Фев", "Мар", "Апр", "Май", "Июн",
            "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек" };

        Календарь = Calendar.getInstance();
        Календарь.setTime( new Date( МенеджерРасписаний.НачалоРаботы ) );

        int ДеньНедели = Календарь.get( Calendar.DAY_OF_WEEK );
        int Год = Календарь.get( Calendar.YEAR );
        int Месяц = Календарь.get( Calendar.MONTH );
        int Число = Календарь.get( Calendar.DAY_OF_MONTH );
        int Минуты = Календарь.get( Calendar.MINUTE );
        int Часы = Календарь.get( Calendar.HOUR_OF_DAY );
        int Секунды = Календарь.get( Calendar.SECOND );

        Текст = Неделя[ ДеньНедели - 1 ]
            + " "
            + Число
            + " "
            + ИмяМесяца[ Месяц ]
            + " "
            + Год;
        Текст += " ";
        Текст += ( ( Часы < 10 ) ? "0" : "" ) + Часы
            + ":"
            + ( ( Минуты < 10) ? "0" : "" ) + Минуты
            + ":"
            + ( ( Секунды < 10 ) ? "0" : "" ) + Секунды;

        // Время начала работы программы
        return Текст;
    }


    public String ПродолжительностьВТекст( long Интервал ) {

        String Текст;

        int Дни = ( int ) ( Интервал / MonitorForm.МИЛЛСЕК_В_ДЕНЬ );

        Интервал -= Дни * MonitorForm.МИЛЛСЕК_В_ДЕНЬ;
        int Часы = ( int ) ( Интервал / MonitorForm.МИЛЛСЕК_В_ЧАС );

        Интервал -= Часы * MonitorForm.МИЛЛСЕК_В_ЧАС;
        int Минуты = ( int ) ( Интервал / MonitorForm.МИЛЛСЕК_В_МИН );

        Интервал -= Минуты * MonitorForm.МИЛЛСЕК_В_МИН;
        int Секунды = ( int ) ( Интервал / Scheduler.МИЛЛСЕК_В_СЕКУНДЕ );

        Текст = Дни + " сут ";
        Текст += ( ( Часы < 10 ) ? "0" : "" ) + Часы
            + " ч "
            + ( ( Минуты < 10 ) ? "0" : "" ) + Минуты
            + " мин "
            + ( ( Секунды < 10 ) ? "0" : "" ) + Секунды
            + " сек";

        return Текст;
    }


    public void Отобразить(){

        Дисплей.setCurrent( Форма );
    }


    // </editor-fold>

    // <editor-fold desc=" Обработчики событий ">

    public void commandAction (Command команда, Displayable элемент) {

        if ( элемент == Форма ) {

            if ( команда == КомандаНазад ) {

                main.Логгер.info( "[AboutForm.java]: <Назад>" );
                Дисплей.setCurrent( РодительскоеОкно );
            }

        }

    }

    // </editor-fold>

}