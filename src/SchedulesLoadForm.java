
// <editor-fold defaultstate="collapsed" desc=" Подключаемые модули ">

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;

// </editor-fold>

public class SchedulesLoadForm implements CommandListener {

    // <editor-fold defaultstate="collapsed" desc=" Поля класса ">

    private Form Форма;

    private ChoiceGroup ПолеВыборРасписаний;

    private MIDlet Мидлет;
    private Display Дисплей;
    private SchedulesForm РодительскоеОкно;
    private Settings Настройки;
    private Schedules МенеджерРасписаний;

    private Command КомандаНазад = new Command( "Назад", Command.BACK, 0 );
    private Command КомандаЗагрузить = new Command( "Загрузить", Command.SCREEN, 1 );

    // </editor-fold>

    // <editor-fold desc=" Конструктор ">

    // Конструктор для создания новой записи.
    public SchedulesLoadForm( MIDlet Мидлет, SchedulesForm Окно, Settings Настройки, Schedules Менеджер ) {

        this.Мидлет = Мидлет;
        Дисплей = Display.getDisplay( Мидлет );
        РодительскоеОкно = Окно;
        this.Настройки = Настройки;
        this.МенеджерРасписаний = Менеджер;

        Форма = new Form ( "Загрузить расписания" );

        if ( RecordStore.listRecordStores() != null ) {

            ПолеВыборРасписаний = new ChoiceGroup( "Записи: ", Choice.POPUP, RecordStore.listRecordStores(), null );
            ПолеВыборРасписаний.setSelectedIndex( 0, true );

        } else {

            ПолеВыборРасписаний = new ChoiceGroup( "Записи: ", Choice.POPUP );
            ПолеВыборРасписаний.append( "пусто", null );
        }

        Форма.append( ПолеВыборРасписаний );

        Форма.addCommand( КомандаНазад );
        Форма.addCommand( КомандаЗагрузить );

        Форма.setCommandListener( this );

        main.Логгер.info( "[SchedulesLoadForm.java]: SchedulesLoadForm()" );
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

            if ( команда == КомандаНазад ) {

                main.Логгер.info( "[SchedulesLoadForm.java]: <Назад>" );
                РодительскоеОкно.Обновить();
                Дисплей.setCurrent( РодительскоеОкно.Расписания );


            } else if ( команда == КомандаЗагрузить ) {

                main.Логгер.info( "[SchedulesLoadForm.java]: <Загрузить>" );
                String Текст;

                Настройки.Расписания.ИмяЗаписиВХранилище = ПолеВыборРасписаний.getString( ПолеВыборРасписаний.getSelectedIndex() );
                МенеджерРасписаний.ЗагрузитьРасписания( Настройки.Расписания.ИмяЗаписиВХранилище );

                Текст = "Расписания загружены из хранилища.";
                Дисплей.setCurrent( new Alert( "Сообщение", Текст, null , AlertType.INFO ) );
            }

        }

    }

    // </editor-fold>
    
}

