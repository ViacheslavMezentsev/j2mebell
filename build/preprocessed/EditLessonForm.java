
// <editor-fold defaultstate="collapsed" desc=" Подключаемые модули ">

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

// </editor-fold>

public class EditLessonForm implements CommandListener {

    // <editor-fold defaultstate="collapsed" desc=" Поля класса ">

    static final public byte РЕЖИМ_ДОБАВИТЬ = 0;
    static final public byte РЕЖИМ_ИЗМЕНИТЬ = 1;

    private byte цРежим;
    private int цРасписание;
    private int цУрок;

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
    public EditLessonForm( MIDlet мидлет, SchedulesForm родитель, Schedules менеджер, int цРасп ){

        Мидлет = мидлет;
        Дисплей = Display.getDisplay( Мидлет );
        РодительскоеОкно = родитель;
        МенеджерРасписаний = менеджер;
        цРасписание = цРасп;

        цРежим = РЕЖИМ_ДОБАВИТЬ;

        Форма = new Form ( "Добавить" );

        ПолеТекста = new TextField( "Урок", "", 16, TextField.ANY );

        Форма.append( ПолеТекста );
        Форма.addCommand( КомандаОк );
        Форма.addCommand( КомандаОтмена );

        Форма.setCommandListener( this );
    }

    // Конструктор для редактирования текущей записи.
    public EditLessonForm( MIDlet мидлет, SchedulesForm родитель, Schedules менеджер, int цРасп, int цУр ){

        Мидлет = мидлет;
        Дисплей = Display.getDisplay( Мидлет );
        РодительскоеОкно = родитель;
        МенеджерРасписаний = менеджер;

        цРасписание = цРасп;
        цУрок = цУр;

        Schedules.ScheduleClass Расписание;
        Schedules.LessonClass Урок;

        Расписание = ( Schedules.ScheduleClass ) МенеджерРасписаний.Расписания.elementAt( цРасписание );
        Урок = ( Schedules.LessonClass ) Расписание.Уроки.elementAt( цУрок );

        цРежим = РЕЖИМ_ИЗМЕНИТЬ;
        Форма = new Form( "Изменить" );

        ПолеТекста = new TextField( "Урок", Урок.Название, 16, TextField.ANY );

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

                        МенеджерРасписаний.ДобавитьУрок( цРасписание, ПолеТекста.getString() );
                        break;

                    case РЕЖИМ_ИЗМЕНИТЬ:

                        МенеджерРасписаний.ИзменитьУрок( цРасписание, цУрок, ПолеТекста.getString() );
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
