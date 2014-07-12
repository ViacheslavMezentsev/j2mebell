
// <editor-fold defaultstate="collapsed" desc=" Подключаемые модули ">

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;

// </editor-fold>

public class SchedulesSaveAsForm implements CommandListener, ItemStateListener {

    // <editor-fold defaultstate="collapsed" desc=" Поля класса ">

    private Form Форма;
    private TextField ПолеТекста;
    private ChoiceGroup ПолеВыборРасписаний;

    private MIDlet Мидлет;
    private Display Дисплей;
    private Displayable РодительскоеОкно;
    private Settings Настройки;
    private Schedules МенеджерРасписаний;

    private Command КомандаНазад = new Command( "Назад", Command.BACK, 0 );
    private Command КомандаСохранить = new Command( "Сохранить", Command.SCREEN, 1 );

    // </editor-fold>

    // <editor-fold desc=" Конструктор ">

    // Конструктор для создания новой записи.
    public SchedulesSaveAsForm( MIDlet Мидлет, Displayable Окно, Settings Настройки, Schedules Менеджер ) {

        this.Мидлет = Мидлет;
        Дисплей = Display.getDisplay( Мидлет );
        РодительскоеОкно = Окно;
        this.Настройки = Настройки;
        this.МенеджерРасписаний = Менеджер;

        Форма = new Form ( "Сохранить расписания" );

        if ( RecordStore.listRecordStores() != null ) {

            ПолеВыборРасписаний = new ChoiceGroup( "Записи: ", Choice.POPUP, RecordStore.listRecordStores(), null );
            ПолеТекста = new TextField( "Имя: ", "", 16, TextField.ANY );
            ПолеВыборРасписаний.setSelectedIndex( 0, true );
            ПолеТекста.setString( Настройки.Расписания.ИмяЗаписиВХранилище );

        } else {

            ПолеВыборРасписаний = new ChoiceGroup( "Записи: ", Choice.POPUP );
            ПолеВыборРасписаний.append( "пусто", null );
            ПолеТекста = new TextField( "Имя: ", Настройки.Расписания.ИмяЗаписиВХранилище, 16, TextField.ANY );
        }

        Форма.append( ПолеВыборРасписаний );
        Форма.append( ПолеТекста );

        Форма.addCommand( КомандаНазад );
        Форма.addCommand( КомандаСохранить );

        Форма.setCommandListener( this );

        main.Логгер.info( "[SchedulesSaveAsForm.java]: SchedulesSaveAsForm()" );
    }

    // </editor-fold>

    // <editor-fold desc=" Методы класса ">

    public void Отобразить() {

        Дисплей.setCurrent( Форма );
    }


    public void Обновить() {

        ПолеВыборРасписаний.deleteAll();

        String[] имена = RecordStore.listRecordStores();

        for ( int ii = 0; ii < имена.length; ii++ ) {

            ПолеВыборРасписаний.append( имена[ii], null );
        }

        if ( ПолеВыборРасписаний.size() > 0 ) {

            ПолеВыборРасписаний.setSelectedIndex( 0, true );
            ПолеТекста.setString( ПолеВыборРасписаний.getString(0) );
        }

    }

    // </editor-fold>

    // <editor-fold desc=" Обработчики событий ">

    public void commandAction( Command команда, Displayable элемент ) {

        if ( элемент == Форма ) {

            if ( команда == КомандаНазад ) {

                main.Логгер.info( "[SchedulesSaveAsForm.java]: <Назад>" );
                Дисплей.setCurrent( РодительскоеОкно );


            } else if ( команда == КомандаСохранить ) {

                main.Логгер.info( "[SchedulesSaveAsForm.java]: <Сохранить>" );
                String Текст;

                Настройки.Расписания.ИмяЗаписиВХранилище = ПолеТекста.getString();
                МенеджерРасписаний.СохранитьРасписания( ПолеТекста.getString() );
                Обновить();

                Текст = "Расписания сохранены в хранилище.";
                Дисплей.setCurrent( new Alert( "Сообщение", Текст, null , AlertType.INFO ) );
            }

        }

    }


    public void itemStateChanged( Item элемент ) {

        if ( элемент == ПолеВыборРасписаний ) {

            ПолеТекста.setString( ПолеВыборРасписаний.getString( ПолеВыборРасписаний.getSelectedIndex() ) );
        }
        
    }

    // </editor-fold>

}


