
// <editor-fold defaultstate="collapsed" desc=" Подключаемые модули ">

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;

// </editor-fold>

public class SettingsLoadForm implements CommandListener {

    // <editor-fold defaultstate="collapsed" desc=" Поля класса ">

    private Form Форма;

    private ChoiceGroup ПолеВыборНастроек;

    private MIDlet Мидлет;
    private Display Дисплей;
    private Displayable РодительскоеОкно;
    private Settings Настройки;

    private Command КомандаНазад = new Command( "Назад", Command.BACK, 0 );
    private Command КомандаЗагрузить = new Command( "Загрузить", Command.SCREEN, 1 );

    // </editor-fold>

    // <editor-fold desc=" Конструктор ">

    // Конструктор для создания новой записи.
    public SettingsLoadForm( MIDlet Мидлет, Displayable Окно, Settings Настройки ) {

        this.Мидлет = Мидлет;
        Дисплей = Display.getDisplay( Мидлет );
        РодительскоеОкно = Окно;
        this.Настройки = Настройки;

        Форма = new Form ( "Загрузить настройки" );

        if ( RecordStore.listRecordStores() != null ) {

            ПолеВыборНастроек = new ChoiceGroup( "Записи: ", Choice.POPUP, RecordStore.listRecordStores(), null );
            ПолеВыборНастроек.setSelectedIndex( 0, true );

        } else {

            ПолеВыборНастроек = new ChoiceGroup( "Записи: ", Choice.POPUP );
            ПолеВыборНастроек.append( "пусто", null );
        }

        Форма.append( ПолеВыборНастроек );

        Форма.addCommand( КомандаНазад );
        Форма.addCommand( КомандаЗагрузить );

        Форма.setCommandListener( this );

        main.Логгер.info( "[SettingsLoadForm.java]: SettingsLoadForm()" );
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

                main.Логгер.info( "[SettingsLoadForm.java]: <Назад>" );
                Дисплей.setCurrent( РодительскоеОкно );


            } else if ( команда == КомандаЗагрузить ) {

                main.Логгер.info( "[SettingsLoadForm.java]: <Загрузить>" );
                String Текст;

                Настройки.Расписания.ИмяЗаписиВХранилище = ПолеВыборНастроек.getString( ПолеВыборНастроек.getSelectedIndex() );
                Настройки.ЗагрузитьНастройки( Настройки.Основные.ИмяВХранилище );

                Текст = "Настройки загружены из хранилища.";
                Дисплей.setCurrent( new Alert( "Сообщение", Текст, null , AlertType.INFO ) );

            }

        }
        
    }

    // </editor-fold>

}

