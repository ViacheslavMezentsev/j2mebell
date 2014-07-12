
// <editor-fold defaultstate="collapsed" desc=" Подключаемые модули ">

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

// </editor-fold>

public class GeneralSettingsForm implements CommandListener, ItemStateListener {

    // <editor-fold defaultstate="collapsed" desc=" Поля класса ">

    private Command КомандаНазад;
    private Command КомандаПрименить;

    private ChoiceGroup ПолеПуть;
    private ChoiceGroup ПолеФайлНастроек;
    private ChoiceGroup ПолеПутьРасписаний;
    private ChoiceGroup ПолеФайлЛога;
    private TextField ПолеРазмерФайлаЛога;
    private ChoiceGroup ПолеКодировка;
    private TextField ПолеПоправкаВремени;
    private Gauge ПолеГромкость;
    private TextField ПолеГромкостьЧисло;

    private Form Форма;

    private MIDlet Мидлет;
    private Display Дисплей;
    private Displayable РодительскоеОкно;
    private Settings Настройки;

    // </editor-fold>

    // <editor-fold desc=" Конструктор ">

    public GeneralSettingsForm( MIDlet Мидлет, Displayable Окно, Settings Настройки ) {

        this.Мидлет = Мидлет;
        Дисплей = Display.getDisplay( Мидлет );
        РодительскоеОкно = Окно;
        this.Настройки = Настройки;

        Форма = new Form ( "Настройки\\Основные" );

        // Создаём команды
        КомандаНазад = new Command( "Назад", Command.BACK, 0 );
        КомандаПрименить = new Command( "Применить", Command.SCREEN, 1 );

        String Путь = Настройки.Основные.МелодииПуть;

        ПолеПуть = new ChoiceGroup( "Путь к файлам: ", Choice.POPUP );
        ПолеПуть.append( ( Путь != null ) ? Путь : "пусто", null );
        ПолеПуть.append( "Обзор...", null );
        ПолеПуть.setSelectedIndex( 0, true );

        Путь = Настройки.Основные.ФайлНастроек;
        ПолеФайлНастроек = new ChoiceGroup( "Файл настроек: ", Choice.POPUP );
        ПолеФайлНастроек.append( ( Путь != null ) ? Путь : "пусто", null );
        ПолеФайлНастроек.append( "Обзор...", null );
        ПолеФайлНастроек.setSelectedIndex( 0, true );

        Путь = Настройки.Основные.ФайлРасписаний;
        ПолеПутьРасписаний = new ChoiceGroup( "Файл расписаний: ", Choice.POPUP );
        ПолеПутьРасписаний.append( ( Путь != null ) ? Путь : "пусто", null );
        ПолеПутьРасписаний.append( "Обзор...", null );
        ПолеПутьРасписаний.setSelectedIndex( 0, true );

        Путь = Настройки.Основные.ФайлЛога;
        ПолеФайлЛога = new ChoiceGroup( "Файл лога: ", Choice.POPUP );
        ПолеФайлЛога.append( ( Путь != null ) ? Путь : "пусто", null );
        ПолеФайлЛога.append( "Обзор...", null );
        ПолеФайлЛога.setSelectedIndex( 0, true );

        ПолеРазмерФайлаЛога = new TextField( "Размер файла лога: ",
            Integer.toString( Настройки.Основные.РазмерФайлаЛога ), 4, TextField.NUMERIC );

        ПолеКодировка = new ChoiceGroup( "Кодировка: ", Choice.POPUP );
        ПолеКодировка.append( Settings.СТРОКА_UTF8, null );
        ПолеКодировка.append( Settings.СТРОКА_CP1251, null );
        ПолеКодировка.setSelectedIndex( Настройки.Основные.Кодировка, true );

        ПолеПоправкаВремени = new TextField( "Суточная поправка ([-]сек): ",
            Integer.toString( Настройки.Основные.СуточнаяПоправка ), 4, TextField.ANY );

        ПолеГромкостьЧисло = new TextField( "Громкость: ",
            Integer.toString( Настройки.Основные.Громкость ), 4, TextField.NUMERIC );
        ПолеГромкость = new Gauge( "", true, 100, Настройки.Основные.Громкость );

        Форма.append( ПолеПуть );
        Форма.append( ПолеФайлНастроек );
        Форма.append( ПолеПутьРасписаний );
        Форма.append( ПолеФайлЛога );
        Форма.append( ПолеРазмерФайлаЛога );
        Форма.append( ПолеКодировка );
        Форма.append( ПолеПоправкаВремени );
        Форма.append( ПолеГромкостьЧисло );
        Форма.append( ПолеГромкость );

        Форма.addCommand( КомандаНазад );
        Форма.addCommand( КомандаПрименить );

        // Установка обработчика событий Формы
        Форма.setCommandListener( this );
        Форма.setItemStateListener( this );

        main.Логгер.info( "[GeneralSettingsForm.java]: GeneralSettingsForm()" );
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

            // Обработка команд.
            // Переход в Главное меню.
            if ( команда == КомандаНазад ) {

                main.Логгер.info( "[GeneralSettingsForm.java]: <Назад>" );
                Дисплей.setCurrent( РодительскоеОкно );

            } else if ( команда == КомандаПрименить ) {

                main.Логгер.info( "[GeneralSettingsForm.java]: <Применить>" );

                Настройки.Основные.ФайлНастроек = ПолеФайлНастроек.getString(0);
                Настройки.Основные.ФайлРасписаний = ПолеПутьРасписаний.getString(0);
                Настройки.Основные.ФайлЛога = ПолеФайлЛога.getString(0);
                Настройки.Основные.РазмерФайлаЛога = Integer.parseInt( ПолеРазмерФайлаЛога.getString() );
                Настройки.Основные.Кодировка = ПолеКодировка.getSelectedIndex();
                Настройки.Основные.СуточнаяПоправка = Integer.parseInt( ПолеПоправкаВремени.getString() );
                Настройки.Основные.Громкость = ПолеГромкость.getValue();

                Дисплей.setCurrent( new Alert( "Сообщение", "Основные настройки сохранены.", null, AlertType.INFO ) );

            }

        }

    }


    public void itemStateChanged( Item элемент ) {

        if ( элемент == ПолеГромкость ) {

            ПолеГромкостьЧисло.setString( Integer.toString( ПолеГромкость.getValue() ) );


        } else if ( элемент == ПолеГромкостьЧисло ) {

            ПолеГромкость.setValue( Integer.parseInt( ПолеГромкостьЧисло.getString() ) );


        } else if ( элемент == ПолеКодировка ) {

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


        } else if ( элемент == ПолеФайлНастроек ) {

            switch ( ПолеФайлНастроек.getSelectedIndex() ) {

                case 0:

                    ( new EditSettingsStringForm( Мидлет, Форма, ПолеФайлНастроек ) ).Отобразить();
                    break;

                case 1:

                    ( new FileBrowserForm( Мидлет, Форма, Настройки, ПолеФайлНастроек ) ).Отобразить();
                    break;
            }


        } else if ( элемент == ПолеПутьРасписаний ) {

            switch ( ПолеПутьРасписаний.getSelectedIndex() ) {

                case 0:

                    ( new EditSettingsStringForm( Мидлет, Форма, ПолеПутьРасписаний ) ).Отобразить();
                    break;

                case 1:

                    ( new FileBrowserForm( Мидлет, Форма, Настройки, ПолеПутьРасписаний ) ).Отобразить();
                    break;
            }


        } else if ( элемент == ПолеФайлЛога ) {

            switch ( ПолеФайлЛога.getSelectedIndex() ) {

                case 0:

                    ( new EditSettingsStringForm( Мидлет, Форма, ПолеФайлЛога ) ).Отобразить();
                    break;

                case 1:

                    ( new FileBrowserForm( Мидлет, Форма, Настройки, ПолеФайлЛога ) ).Отобразить();
                    break;
            }

        }
        
    }

    // </editor-fold>

}
