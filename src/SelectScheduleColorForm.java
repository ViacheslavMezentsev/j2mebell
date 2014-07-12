
// <editor-fold defaultstate="collapsed" desc=" Подключаемые модули ">

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

// </editor-fold>

public class SelectScheduleColorForm implements CommandListener {

    // <editor-fold defaultstate="collapsed" desc=" Поля класса ">

    private Image КартинкаЦвета;

    private List Список;

    private MIDlet Мидлет;
    private Display Дисплей;
    private Displayable РодительскоеОкно;
    private Schedules МенеджерРасписаний;
    private Planner Планировщик;

    private Command КомандаНазад = new Command( "Назад", Command.BACK, 0 );
    private Command КомандаВыбрать = new Command( "Выбрать", Command.SCREEN, 1 );

    // </editor-fold>

    // <editor-fold desc=" Конструктор ">

    // Конструктор для создания новой записи.
    public SelectScheduleColorForm( MIDlet Мидлет, Displayable окно, Schedules Менеджер, Planner Планировщик ) {

        this.Мидлет = Мидлет;
        Дисплей = Display.getDisplay( Мидлет );
        РодительскоеОкно = окно;
        МенеджерРасписаний = Менеджер;
        this.Планировщик = Планировщик;

        Список = new List ( "Расписания", List.IMPLICIT );

        int[] rgb = new int[ 16 * 16 ];

        Schedules.ScheduleClass Расписание;

        for ( int ii = 0; ii < МенеджерРасписаний.Расписания.size(); ii++ ) {

            Расписание = ( Schedules.ScheduleClass ) МенеджерРасписаний.Расписания.elementAt(ii);

            int цвет = Планировщик.ЦветРасписанияN( Планировщик.НомерЗаписиРасписанияN(ii) );

            for ( int jj = 0; jj < 16 * 16; jj++ ) {
                if ( ( ( jj % 16 ) == 0 )
                    || ( ( jj % 16 ) == 15 )
                    || ( ( jj > 0) && ( jj < 16 ) )
                    || ( ( jj > 16*15) && ( jj < 16*16 ) ) ) {

                    rgb[jj] = 0;
                    continue;
                }

                rgb[jj] = цвет;
            }

            КартинкаЦвета = Image.createRGBImage( rgb, 16, 16, false );
            Список.append( Расписание.Название, КартинкаЦвета );
        }

        if ( Список.size() > 0 ) Список.setSelectedIndex( Планировщик.НомерРасписания, true);

        Список.addCommand( КомандаНазад );
        Список.addCommand( КомандаВыбрать );

        Список.setCommandListener( this );
    }

    // </editor-fold>

    // <editor-fold desc=" Методы класса ">

    public void Отобразить() {

        Дисплей.setCurrent( Список );
    }

    // </editor-fold>

    // <editor-fold desc=" Обработчики событий ">

    public void commandAction( Command команда, Displayable элемент ) {

        if ( элемент == Список ) {

            if ( ( команда == List.SELECT_COMMAND ) || ( команда == КомандаВыбрать ) ) {

                Планировщик.НомерРасписания = Список.getSelectedIndex();
                Дисплей.setCurrent( РодительскоеОкно );

            } else if ( команда == КомандаНазад ) {

                Дисплей.setCurrent( РодительскоеОкно );
            }

        }

    }

    // </editor-fold>
    
}
