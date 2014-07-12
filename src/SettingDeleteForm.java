
// <editor-fold defaultstate="collapsed" desc=" Подключаемые модули ">

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

// </editor-fold>

public class SettingDeleteForm implements CommandListener {

    // <editor-fold defaultstate="collapsed" desc=" Поля класса ">

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
    public SettingDeleteForm( MIDlet Мидлет, Displayable Окно, Settings Настройки ) {

        this.Мидлет = Мидлет;
        Дисплей = Display.getDisplay( Мидлет );
        РодительскоеОкно = Окно;
        this.Настройки = Настройки;

        Форма = new Form ( "Удалить настройки" );
//        ПолеВыборНастроек = new ChoiceGroup( "Запись: ", Choice.POPUP );
//        // Перебираем по именам и находим текущий набор
//        ПолеВыборНастроек.setSelectedIndex( Настройки.Расписания.ТекущееРасписание, true );
//        Форма.append( ПолеВыборНастроек );
//
//        ПолеТекста = new TextField( "Имя: ", "", 16, TextField.ANY );
//        Форма.append( ПолеТекста );

        Форма.addCommand( КомандаОк );
        Форма.addCommand( КомандаОтмена );

        Форма.setCommandListener( this );

        main.Логгер.info( "[SettingDeleteForm.java]: SettingDeleteForm()" );
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

                main.Логгер.info( "[SettingDeleteForm.java]: <Ок>" );
                Дисплей.setCurrent( РодительскоеОкно );


            } else if ( команда == КомандаОтмена ) {

                main.Логгер.info( "[SettingDeleteForm.java]: <Отмена>" );
                Дисплей.setCurrent( РодительскоеОкно );
            }

        }

    }

    // </editor-fold>

}

