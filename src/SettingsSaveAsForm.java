
// <editor-fold defaultstate="collapsed" desc=" Подключаемые модули ">

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;

// </editor-fold>

public class SettingsSaveAsForm implements CommandListener, ItemStateListener {

    // <editor-fold defaultstate="collapsed" desc=" Поля класса ">

    private Form Форма;

    private TextField ПолеТекста;
    private ChoiceGroup ПолеВыборНастроек;

    private MIDlet Мидлет;
    private Display Дисплей;
    private Displayable РодительскоеОкно;
    private Settings Настройки;

    private Command КомандаНазад = new Command( "Назад", Command.BACK, 0 );
    private Command КомандаСохранить = new Command( "Сохранить", Command.SCREEN, 1 );

    // </editor-fold>

    // <editor-fold desc=" Конструктор ">

    // Конструктор для создания новой записи.
    public SettingsSaveAsForm( MIDlet Мидлет, Displayable Окно, Settings Настройки ) {

        this.Мидлет = Мидлет;
        Дисплей = Display.getDisplay( Мидлет );
        РодительскоеОкно = Окно;
        this.Настройки = Настройки;

        Форма = new Form ( "Сохранить настройки" );

        if ( RecordStore.listRecordStores() != null ) {

            ПолеВыборНастроек = new ChoiceGroup( "Записи: ", Choice.POPUP, RecordStore.listRecordStores(), null );
            ПолеТекста = new TextField( "Имя: ", "", 16, TextField.ANY );
            ПолеВыборНастроек.setSelectedIndex( 0, true );
            ПолеТекста.setString( Настройки.Основные.ИмяВХранилище );

        } else {

            ПолеВыборНастроек = new ChoiceGroup( "Записи: ", Choice.POPUP );
            ПолеВыборНастроек.append( "пусто", null );
            ПолеТекста = new TextField( "Имя: ", Настройки.Основные.ИмяВХранилище, 16, TextField.ANY );
        }

        Форма.append( ПолеВыборНастроек );
        Форма.append( ПолеТекста );

        Форма.addCommand( КомандаНазад );
        Форма.addCommand( КомандаСохранить );

        Форма.setCommandListener( this );

        main.Логгер.info( "[SettingsSaveAsForm.java]: SettingsSaveAsForm()" );
    }

    // </editor-fold>

    // <editor-fold desc=" Методы класса ">

    public void Отобразить() {

        Дисплей.setCurrent( Форма );
    }

    public void Обновить() {

        ПолеВыборНастроек.deleteAll();

        String[] имена = RecordStore.listRecordStores();

        for ( int ii = 0; ii < имена.length; ii++ ) {

            ПолеВыборНастроек.append( имена[ii], null );
        }

        if ( ПолеВыборНастроек.size() > 0 ) {

            ПолеВыборНастроек.setSelectedIndex( 0, true );
            ПолеТекста.setString( ПолеВыборНастроек.getString(0) );
        }

    }

    // </editor-fold>

    // <editor-fold desc=" Обработчики событий ">

    public void commandAction( Command команда, Displayable элемент ) {

        if ( элемент == Форма ) {

            if ( команда == КомандаНазад ) {

                main.Логгер.info( "[SettingsSaveAsForm.java]: <Назад>" );
                Дисплей.setCurrent( РодительскоеОкно );


            } else if ( команда == КомандаСохранить ) {

                main.Логгер.info( "[SettingsSaveAsForm.java]: <Сохранить>" );
                String Текст;

                Настройки.Основные.ИмяВХранилище = ПолеТекста.getString();
                Настройки.СохранитьНастройки( ПолеТекста.getString() );
                Обновить();

                Текст = "Настройки сохранены в хранилище.";
                Дисплей.setCurrent( new Alert( "Сообщение", Текст, null , AlertType.INFO ) );

            }

        }

    }


    public void itemStateChanged( Item элемент ) {

        if ( элемент == ПолеВыборНастроек ) {

            ПолеТекста.setString( ПолеВыборНастроек.getString( ПолеВыборНастроек.getSelectedIndex() ) );
        }
        
    }

    // </editor-fold>

}

