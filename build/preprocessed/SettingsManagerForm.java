
// <editor-fold defaultstate="collapsed" desc=" Подключаемые модули ">

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

// </editor-fold>

public class SettingsManagerForm implements CommandListener {

    // <editor-fold defaultstate="collapsed" desc=" Поля класса ">

    static final public byte РЕЖИМ_СОХРАНИТЬ_КАК = 0;
    static final public byte РЕЖИМ_ЗАГРУЗИТЬ = 1;
    static final public byte РЕЖИМ_УДАЛИТЬ = 2;
    static final public byte РЕЖИМ_ИМПОРТ = 3;
    static final public byte РЕЖИМ_ЭКСПОРТ = 4;

    private int цРежим;

    private Form Форма;
    private TextField ПолеТекста;
    private ChoiceGroup ПолеВыборНастроек;

    private MIDlet Мидлет;
    private Display Дисплей;
    private Displayable РодительскоеОкно;
    private Settings Настройки;

    private Command КомандаОк = new Command( "Ок", Command.OK, 0 );
    private Command КомандаОтмена = new Command( "Отмена", Command.CANCEL, 1 );

    // </editor-fold>

    // <editor-fold desc=" Конструктор ">

    // Конструктор для создания новой записи.
    public SettingsManagerForm( MIDlet Мидлет, Displayable Окно, Settings Настройки, int цРежим ) {

        this.Мидлет = Мидлет;
        Дисплей = Display.getDisplay( Мидлет );
        РодительскоеОкно = Окно;
        this.Настройки = Настройки;

        this.цРежим = цРежим;

        switch ( цРежим ) {

            case РЕЖИМ_СОХРАНИТЬ_КАК:

                Форма = new Form ( "Сохранить настройки" );
                ПолеВыборНастроек = new ChoiceGroup( "Запись: ", Choice.POPUP );
                // Перебираем по именам и находим текущий набор
                ПолеВыборНастроек.setSelectedIndex( Настройки.Расписания.ТекущееРасписание, true );
                Форма.append( ПолеВыборНастроек );

                ПолеТекста = new TextField( "Имя: ", "", 16, TextField.ANY );
                Форма.append( ПолеТекста );
                break;

            case РЕЖИМ_ЗАГРУЗИТЬ:

                Форма = new Form ( "Загрузить настройки" );
                ПолеВыборНастроек = new ChoiceGroup( "Запись: ", Choice.POPUP );
                // Перебираем по именам и находим текущий набор
                ПолеВыборНастроек.setSelectedIndex( Настройки.Расписания.ТекущееРасписание, true );
                Форма.append( ПолеВыборНастроек );
                break;

            case РЕЖИМ_УДАЛИТЬ:

                Форма = new Form ( "Удалить настройки" );
                ПолеВыборНастроек = new ChoiceGroup( "Запись: ", Choice.POPUP );
                // Перебираем по именам и находим текущий набор
                ПолеВыборНастроек.setSelectedIndex( Настройки.Расписания.ТекущееРасписание, true );
                Форма.append( ПолеВыборНастроек );
                break;
        }

        Форма.addCommand( КомандаОк );
        Форма.addCommand( КомандаОтмена );

        Форма.setCommandListener( this );

        main.Логгер.info( "[SettingsManagerForm.java]: SettingsManagerForm()" );
    }

    // </editor-fold>

    // <editor-fold desc=" Методы класса ">

    public void Отобразить() {

        Дисплей.setCurrent( Форма );
    }

    public void СохранитьКак( String ИмяЗаписи ) {

    }

    public void Загрузить( String ИмяЗаписи ) {

    }

    public void Удалить( String ИмяЗаписи ) {

    }

    // </editor-fold>

    // <editor-fold desc=" Обработчики событий ">

    public void commandAction( Command команда, Displayable элемент ) {

        if ( элемент == Форма ) {

            if ( команда == КомандаОк ) {

                switch ( цРежим ) {

                    case РЕЖИМ_СОХРАНИТЬ_КАК:
                        break;

                    case РЕЖИМ_ЗАГРУЗИТЬ:
                        break;

                    case РЕЖИМ_УДАЛИТЬ:
                        break;
                }

                Дисплей.setCurrent( РодительскоеОкно );


            } else if ( команда == КомандаОтмена ) {

                Дисплей.setCurrent( РодительскоеОкно );
            }

        }
        
    }

    // </editor-fold>

}

