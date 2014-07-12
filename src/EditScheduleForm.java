
// <editor-fold defaultstate="collapsed" desc=" Подключаемые модули ">

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

// </editor-fold>

public class EditScheduleForm implements CommandListener {

    // <editor-fold defaultstate="collapsed" desc=" Поля класса ">

    static final public byte РЕЖИМ_ДОБАВИТЬ = 0;
    static final public byte РЕЖИМ_ИЗМЕНИТЬ = 1;

    private byte цРежим;
    private int цИндекс;

    private Form Форма;
    private TextField ПолеТекста;

    private MIDlet Мидлет;
    private Display Дисплей;
    private SchedulesForm РодительскоеОкно;
    private Schedules МенеджерРасписаний;

    private Command КомандаОк = new Command( "Ок", Command.OK, 0 );
    private Command КомандаОтмена = new Command( "Отмена", Command.CANCEL, 1 );

    // </editor-fold>

    // <editor-fold desc=" Конструктор ">

    // Конструктор для создания новой записи.
    public EditScheduleForm( MIDlet мидлет, SchedulesForm родитель, Schedules менеджер ) {

        Мидлет = мидлет;
        Дисплей = Display.getDisplay( Мидлет );
        РодительскоеОкно = родитель;
        МенеджерРасписаний = менеджер;

        цРежим = РЕЖИМ_ДОБАВИТЬ;

        Форма = new Form ( "Добавить" );

        ПолеТекста = new TextField( "Расписание", "", 16, TextField.ANY );

        Форма.append( ПолеТекста );
        Форма.addCommand( КомандаОк );
        Форма.addCommand( КомандаОтмена );

        Форма.setCommandListener( this );
    }

    // Конструктор для редактирования текущей записи.
    public EditScheduleForm( MIDlet мидлет, SchedulesForm родитель, Schedules менеджер, int цРасписание ) {

        Мидлет = мидлет;
        Дисплей = Display.getDisplay( Мидлет );
        РодительскоеОкно = родитель;
        МенеджерРасписаний = менеджер;

        цИндекс = цРасписание;

        Schedules.ScheduleClass Расписание;
        Расписание = ( Schedules.ScheduleClass ) МенеджерРасписаний.Расписания.elementAt( цИндекс );

        цРежим = РЕЖИМ_ИЗМЕНИТЬ;
        Форма = new Form ( "Изменить" );

        ПолеТекста = new TextField( "Расписание", Расписание.Название, 16, TextField.ANY );

        Форма.append( ПолеТекста );
        Форма.addCommand( КомандаОк );
        Форма.addCommand( КомандаОтмена );

        Форма.setCommandListener( this );
    }

    // </editor-fold>

    // <editor-fold desc=" Методы класса ">

    public void Отобразить() {

        Дисплей.setCurrent( Форма );
    }

    // </editor-fold>

    // <editor-fold desc=" Обработчики событий ">

    public void commandAction( Command команда, Displayable элемент ) {

        if ( элемент == Форма ) {

            if ( команда == КомандаОк ) {

                switch ( цРежим ) {

                    case РЕЖИМ_ДОБАВИТЬ:

                        МенеджерРасписаний.ДобавитьРасписание( ПолеТекста.getString() );
                        break;

                    case РЕЖИМ_ИЗМЕНИТЬ:

                        МенеджерРасписаний.ИзменитьРасписание( цИндекс, ПолеТекста.getString() );
                        break;

                }

                РодительскоеОкно.Обновить();
                Дисплей.setCurrent( РодительскоеОкно.Расписания );


            } else if ( команда == КомандаОтмена ) {

                РодительскоеОкно.Обновить();
                Дисплей.setCurrent( РодительскоеОкно.Расписания );
            }

        }

    }

    // </editor-fold>

}
