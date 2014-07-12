
// <editor-fold defaultstate="collapsed" desc=" Подключаемые модули ">

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

// </editor-fold>

public class SettingsImportForm implements CommandListener, ItemStateListener {

    // <editor-fold defaultstate="collapsed" desc=" Поля класса ">

    private Form Форма;

    private ChoiceGroup ПолеКодировка;
    private ChoiceGroup ПолеИмяФайла;

    private MIDlet Мидлет;
    private Display Дисплей;
    private Displayable РодительскоеОкно;
    private Settings Настройки;

    private Command КомандаНазад = new Command( "Назад", Command.BACK, 0 );
    private Command КомандаИмпорт = new Command( "Импорт", Command.SCREEN, 1 );

    // </editor-fold>

    // <editor-fold desc=" Конструктор ">

    // Конструктор для создания новой записи.
    public SettingsImportForm( MIDlet Мидлет, Displayable Окно, Settings Настройки ) {

        this.Мидлет = Мидлет;
        Дисплей = Display.getDisplay( Мидлет );
        РодительскоеОкно = Окно;
        this.Настройки = Настройки;

        Форма = new Form ( "Импорт настроек" );

        ПолеКодировка = new ChoiceGroup( "Кодировка: ", Choice.POPUP );
        ПолеКодировка.append( Settings.СТРОКА_UTF8, null );
        ПолеКодировка.append( Settings.СТРОКА_CP1251, null );
        ПолеКодировка.setSelectedIndex( Настройки.Основные.Кодировка, true );

        ПолеИмяФайла = new ChoiceGroup( "Путь к файлу: ", Choice.POPUP );
        ПолеИмяФайла.append( ( Настройки.Основные.ФайлНастроек != null ) ? Настройки.Основные.ФайлНастроек : "пусто", null );
        ПолеИмяФайла.append( "Обзор...", null );
        ПолеИмяФайла.setSelectedIndex( 0, true );

        Форма.append( ПолеКодировка );
        Форма.append( ПолеИмяФайла );

        Форма.addCommand( КомандаИмпорт );
        Форма.addCommand( КомандаНазад );

        Форма.setCommandListener( this );
        Форма.setItemStateListener( this );

        main.Логгер.info( "[SettingsImportForm.java]: SettingsImportForm()" );
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

            if ( команда == КомандаИмпорт ) {

                main.Логгер.info( "[SettingsImportForm.java]: <Импорт>" );

                final ProgressForm ОкноПрогресса = new ProgressForm( Мидлет, Форма, Настройки, ProgressForm.РЕЖИМ_ИМПОРТ );

                ОкноПрогресса.Отобразить();

                // Визуализация импорта данных из файла.
                new Thread (

                    new Runnable() {

                        public void run() {

                            Настройки.ИмпортНастроек( ПолеИмяФайла.getString(0), ПолеКодировка.getSelectedIndex() );

                            String Текст = "Настройки загружены из файла: \n" + ПолеИмяФайла.getString(0) + "\n" +
                                    "в кодировке ";

                            switch ( ПолеКодировка.getSelectedIndex() ) {

                                case Settings.КОДИРОВКА_UTF8:

                                    Текст += Settings.СТРОКА_UTF8 + ".";
                                    break;

                                case Settings.КОДИРОВКА_CP1251:

                                    Текст += Settings.СТРОКА_CP1251 + ".";
                                    break;
                            }

                            ОкноПрогресса.Таймер.cancel();

                            Дисплей.setCurrent( Форма );
                            Дисплей.setCurrent( new Alert( "Сообщение", Текст, null, AlertType.INFO ) );
                        }

                    }

                ).start();


            } else if ( команда == КомандаНазад ) {

                main.Логгер.info( "[SettingsImportForm.java]: <Назад>" );
                Дисплей.setCurrent( РодительскоеОкно );
            }

        }

    }


    public void itemStateChanged( Item элемент ) {

        if ( элемент == ПолеКодировка ) {

            String Текст;

            switch ( ПолеКодировка.getSelectedIndex() ) {

                case Settings.КОДИРОВКА_UTF8:

                    Текст = "Текущая кодировка изменена на " + Settings.СТРОКА_UTF8 + ".";
                    Дисплей.setCurrent( new Alert( "Сообщение", Текст, null , AlertType.INFO ) );
                    break;

                case Settings.КОДИРОВКА_CP1251:

                    Текст = "Текущая кодировка изменена на " + Settings.СТРОКА_CP1251 + ".";
                    Дисплей.setCurrent( new Alert( "Сообщение", Текст, null , AlertType.INFO ) );
                    break;
            }

            Настройки.Основные.Кодировка = ПолеКодировка.getSelectedIndex();


        } else if ( элемент == ПолеИмяФайла ) {

            switch ( ПолеИмяФайла.getSelectedIndex() ) {

                case 0:
                    ( new EditSettingsStringForm( Мидлет, Форма, ПолеИмяФайла ) ).Отобразить();
                    break;

                case 1:
                    ( new FileBrowserForm( Мидлет, Форма, Настройки, ПолеИмяФайла ) ).Отобразить();
                    break;
            }

        }
        
    }

    // </editor-fold>

}

