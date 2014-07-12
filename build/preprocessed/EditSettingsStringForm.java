
// <editor-fold defaultstate="collapsed" desc=" Подключаемые модули ">

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

// </editor-fold>

public class EditSettingsStringForm implements CommandListener {

    // <editor-fold defaultstate="collapsed" desc=" Поля класса ">

    ChoiceGroup ПолеВыбора;

    private Form Форма;
    private TextField ПолеТекста;

    private MIDlet Мидлет;
    private Display Дисплей;
    private Displayable РодительскоеОкно;

    private Command КомандаОк = new Command( "Ок", Command.OK, 0 );
    private Command КомандаОтмена = new Command( "Отмена", Command.CANCEL, 1 );

    // </editor-fold>

    // <editor-fold desc=" Конструктор ">

    public EditSettingsStringForm( MIDlet мидлет, Displayable Окно, ChoiceGroup ПолеВыбора ) {

        Мидлет = мидлет;
        Дисплей = Display.getDisplay( Мидлет );
        РодительскоеОкно = Окно;
        this.ПолеВыбора = ПолеВыбора;

        Форма = new Form ( "Изменить" );

        ПолеТекста = new TextField( "Текст: ", ПолеВыбора.getString(0), 256, TextField.ANY );

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

                ПолеВыбора.set( 0, ПолеТекста.getString(), null );
                Дисплей.setCurrent( РодительскоеОкно );


            } else if ( команда == КомандаОтмена ) {

                Дисплей.setCurrent( РодительскоеОкно );
            }

        }
        
    }

    // </editor-fold>

}
