
// <editor-fold defaultstate="collapsed" desc=" Подключаемые модули ">

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

import java.util.*;

// </editor-fold>

public class ProgressForm implements CommandListener {

    // <editor-fold defaultstate="collapsed" desc=" Поля класса ">

    static final public byte РЕЖИМ_ИМПОРТ = 0;
    static final public byte РЕЖИМ_ЭКСПОРТ = 1;

    static final private String ТЕКСТ_ИМПОРТ = "Идёт импорт данных";
    static final private String ТЕКСТ_ЭКСПОРТ = "Идёт экспорт данных";

    static final private String ИНДИКАТОР = "-\\|/";

    private Form Форма;

    private MIDlet Мидлет;
    private Display Дисплей;
    private Displayable РодительскоеОкно;
    private Settings Настройки;

    public Timer Таймер;

    private Command КомандаНазад = new Command( "Назад", Command.BACK, 0 );

    private int Прогресс;
    private byte Режим;

    // </editor-fold>

    // <editor-fold desc=" Конструктор ">

    // Конструктор для создания новой записи.
    public ProgressForm( MIDlet мидлет, Displayable Окно, Settings Настройки, byte Режим ) {

        Мидлет = мидлет;
        Дисплей = Display.getDisplay( Мидлет );
        РодительскоеОкно = Окно;
        this.Настройки = Настройки;
        this.Режим = Режим;

        Прогресс = 0;

        StringItem ПолеТекста;

        switch ( Режим ) {

            case РЕЖИМ_ИМПОРТ:

                Форма = new Form( "Импорт данных [-]" );
                ПолеТекста = new StringItem( ТЕКСТ_ИМПОРТ, "" );
                Форма.append( ПолеТекста );
                break;

            case РЕЖИМ_ЭКСПОРТ:

                Форма = new Form( "Экспорт данных [-]" );
                ПолеТекста = new StringItem( ТЕКСТ_ЭКСПОРТ, "" );
                Форма.append( ПолеТекста );
                break;
        }

        Форма.addCommand( КомандаНазад );

        Таймер = new Timer();
        Таймер.schedule( new TimerTask() { public void run() { ОбновитьИндикатор(); } }, 0, 1000 );

        Форма.setCommandListener( this );

        main.Логгер.info( "[ProgressForm.java]: ProgressForm()" );
    }

    // </editor-fold>

    // <editor-fold desc=" Методы класса ">

    public void Отобразить() {

        Дисплей.setCurrent( Форма );
    }


    public void ОбновитьИндикатор() {

        String Точки;

        Прогресс++;

        switch ( Режим ) {

            case РЕЖИМ_ИМПОРТ:

                Форма.setTitle( "Импорт данных [" + ИНДИКАТОР.charAt( Прогресс % 4 ) + "]" );
                break;

            case РЕЖИМ_ЭКСПОРТ:

                Форма.setTitle( "Экспорт данных [" + ИНДИКАТОР.charAt( Прогресс % 4 ) + "]" );
                break;
        }

        Точки = "[" + Прогресс + "]: ";
        StringItem ПолеТекста = ( StringItem ) Форма.get( 0 );

        for ( int ii = 0; ii < Прогресс; ii++ ) Точки += "*";

        ПолеТекста.setText( Точки );

//        main.Логгер.info( "[ProgressForm.java]: " + Прогресс );
    }

    // </editor-fold>

    // <editor-fold desc=" Обработчики событий ">
    public void commandAction( Command команда, Displayable элемент ) {

        if ( элемент == Форма ) {

            if ( команда == КомандаНазад ) {

                Дисплей.setCurrent( РодительскоеОкно );
            }

        }

    }

    // </editor-fold>

}

