
// <editor-fold desc=" Подключаемые модули ">

import java.io.IOException;
import java.util.Enumeration;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

// </editor-fold>

public class SchedulesForm implements CommandListener {

    // <editor-fold defaultstate="collapsed" desc=" Поля класса ">

    static final public byte УРОВЕНЬ_РАСПИС = 0;
    static final public byte УРОВЕНЬ_УРОК = 1;
    static final public byte УРОВЕНЬ_ЗВОНОК = 2;

    private byte цУровень;
    private int цРасписание;
    private int цУрок;
    private int цЗвонок;

    private Image ИконкаПапки;
    private Image ИконкаФайла;

    // Команды.
    private Command КомандаМеню;
    private Command КомандаНазад;

    private Command КомандаДобавить;
    private Command КомандаИзменить;
    private Command КомандаУдалить;
    private Command КомандаОчистить;
    private Command КомандаСохранить;
    private Command КомандаСохранитьКак;
    private Command КомандаЗагрузить;
    private Command КомандаИмпорт;
    private Command КомандаЭкспорт;

    public List Расписания;

    private MIDlet Мидлет;
    private Display Дисплей;
    private Displayable РодительскоеОкно;
    private Schedules МенеджерРасписаний;
    private Settings Настройки;

    // </editor-fold>

    // <editor-fold desc=" Конструктор ">

    SchedulesForm( MIDlet мидлет, Displayable parent, Schedules менеджер, Settings Настройки ) {

        Мидлет = мидлет;
        Дисплей = Display.getDisplay( Мидлет );
        РодительскоеОкно = parent;
        МенеджерРасписаний = менеджер;
        this.Настройки = Настройки;
        ЗагрузкаИконок();

        КомандаНазад = new Command( "Назад", Command.BACK, 0 );
        КомандаМеню = new Command( "Меню", Command.SCREEN, 1 );

        КомандаДобавить = new Command( "Добавить...", Command.SCREEN, 2 );
        КомандаИзменить = new Command( "Изменить...", Command.SCREEN, 3 );
        КомандаУдалить = new Command( "Удалить", Command.SCREEN, 4 );
        КомандаОчистить = new Command( "Очистить", Command.SCREEN, 5 );

        КомандаСохранить = new Command( "Сохранить", Command.SCREEN, 6 );
        КомандаСохранитьКак = new Command( "Сохранить как...", Command.SCREEN, 7 );
        КомандаЗагрузить = new Command( "Загрузить...", Command.SCREEN, 8 );
        КомандаИмпорт = new Command( "Импорт...", Command.SCREEN, 9 );
        КомандаЭкспорт = new Command( "Экспорт...", Command.SCREEN, 10 );

        // Экран Расписания.
        Расписания = new List( "Расписания", List.IMPLICIT );

        Расписания.addCommand( КомандаНазад );
        Расписания.addCommand( КомандаМеню );
        Расписания.addCommand( КомандаДобавить );
        Расписания.addCommand( КомандаИзменить );
        Расписания.addCommand( КомандаУдалить );
        Расписания.addCommand( КомандаОчистить );
        Расписания.addCommand( КомандаСохранить );
        Расписания.addCommand( КомандаСохранитьКак );
        Расписания.addCommand( КомандаЗагрузить );
        Расписания.addCommand( КомандаИмпорт );
        Расписания.addCommand( КомандаЭкспорт );

        Расписания.setCommandListener( this );

        main.Логгер.info( "[SchedulesForm.java]: SchedulesForm()" );
    }

    // </editor-fold>

    // <editor-fold desc=" Методы класса ">

    public void Отобразить() {

        Schedules.ScheduleClass Расписание;

        // Очищаем список.
        Расписания.deleteAll();

        // Отображение текущего уровня.
        Расписания.append( "..", null );
        for ( Enumeration e = МенеджерРасписаний.Расписания.elements(); e.hasMoreElements(); ) {
            Расписание = ( Schedules.ScheduleClass ) e.nextElement();
            Расписания.append( Расписание.Название, ИконкаПапки );
        }
        цРасписание = 0;
        МенеджерРасписаний.цВыбранныйЭлемент = цРасписание;
        Расписания.setSelectedIndex( 0, true );
        цУровень = УРОВЕНЬ_РАСПИС;

        Дисплей.setCurrent( Расписания );

    }


    public void ЗагрузкаИконок() {

        try {

            ИконкаПапки = Image.createImage( "/icons/dir.png" );

        } catch ( IOException Исключение ) {

            ИконкаПапки = null;
            main.Логгер.error( "[SchedulesForm.java]: " + Исключение.toString() );
        }

        try {

            ИконкаФайла = Image.createImage( "/icons/file.png" );

        } catch ( IOException Исключение ) {

            ИконкаФайла = null;
            main.Логгер.error( "[SchedulesForm.java]: " + Исключение.toString() );
        }

    }


    public void Обновить() {

        String Текст;

        Schedules.ScheduleClass Расписание;
        Schedules.LessonClass Урок;
        Schedules.BellClass Звонок;

        switch ( цУровень ) {

            case УРОВЕНЬ_РАСПИС:

                main.Логгер.info( "[SchedulesForm.java]: \\Меню\\Расписания\\" );
                Расписания.deleteAll();
                Расписания.setTitle( "Расписания" );
                Расписания.append( "..", null );
                for ( Enumeration e = МенеджерРасписаний.Расписания.elements(); e.hasMoreElements(); ) {
                    Расписание = ( Schedules.ScheduleClass ) e.nextElement();
                    Расписания.append( Расписание.Название, ИконкаПапки );
                }
                Расписания.setSelectedIndex( цРасписание + 1, true );
                break;

            case УРОВЕНЬ_УРОК:

                Расписания.deleteAll();
                Расписание = ( Schedules.ScheduleClass ) МенеджерРасписаний.Расписания.elementAt( цРасписание );
                main.Логгер.info( "[SchedulesForm.java]: \\Меню\\Расписания\\"
                        + Расписание.Название + "\\" );
                Расписания.setTitle( Расписание.Название );
                Расписания.append( "..", null );
                for ( Enumeration e = Расписание.Уроки.elements(); e.hasMoreElements(); ) {
                    Урок = ( Schedules.LessonClass ) e.nextElement();
                    Расписания.append( Урок.Название, ИконкаПапки );
                }
                Расписания.setSelectedIndex( цУрок + 1, true );
                break;

            case УРОВЕНЬ_ЗВОНОК:

                Расписания.deleteAll();
                Расписание = ( Schedules.ScheduleClass ) МенеджерРасписаний.Расписания.elementAt( цРасписание );
                Урок = ( Schedules.LessonClass ) Расписание.Уроки.elementAt( цУрок );
                main.Логгер.info( "[SchedulesForm.java]: \\Меню\\Расписания\\"
                        + Расписание.Название + "\\" + Урок.Название + "\\" );
                Расписания.setTitle( Расписание.Название + "\\" + Урок.Название );
                Расписания.append( "..", null );
                for ( Enumeration e = Урок.Звонки.elements(); e.hasMoreElements(); ) {
                    Звонок = ( Schedules.BellClass ) e.nextElement();
                    Текст = "" + ( ( Звонок.цЧасы < 10 ) ? "0" : "" ) + Звонок.цЧасы +
                        "." + ( ( Звонок.цМинуты < 10 ) ? "0" : "" ) + Звонок.цМинуты;
                    Расписания.append( Текст, ИконкаФайла );
                }
                Расписания.setSelectedIndex( цЗвонок + 1, true );
                break;
        }

    }

    // </editor-fold>

    // <editor-fold desc=" Обработчики событий ">

    public void commandAction( Command команда, Displayable элемент ) {

        Schedules.ScheduleClass Расписание;
        Schedules.LessonClass Урок;
        Schedules.BellClass Звонок;
        String Текст;

        if ( элемент == Расписания ) {

            // Переход в Главное меню.
            if ( команда == КомандаМеню ) {

                main.Логгер.info( "[SchedulesForm.java]: <Меню>" );
                Дисплей.setCurrent( РодительскоеОкно );

            // Переход назад.
            } else if ( команда == КомандаНазад ) {

                main.Логгер.info( "[SchedulesForm.java]: <Назад>" );

                switch ( цУровень ) {

                    case УРОВЕНЬ_РАСПИС:

                        Дисплей.setCurrent( РодительскоеОкно );
                        break;

                    case УРОВЕНЬ_УРОК:

                        цУровень = УРОВЕНЬ_РАСПИС;
                        Обновить();
                        break;

                    case УРОВЕНЬ_ЗВОНОК:

                        цУровень = УРОВЕНЬ_УРОК;
                        Обновить();
                        break;
                }

            } else if ( команда == List.SELECT_COMMAND ) {

                int ii = Расписания.getSelectedIndex();

                // Если выбрана папка верхнего уровня.
                if ( ii == 0 ) {

                    switch ( цУровень ) {

                        case УРОВЕНЬ_РАСПИС:

                            цРасписание = 0;
                            МенеджерРасписаний.цВыбранныйЭлемент = цРасписание;
                            main.Логгер.info( "[SchedulesForm.java]: \\Меню\\" );
                            // Смена экрана
                            Дисплей.setCurrent( РодительскоеОкно );
                            break;

                        case УРОВЕНЬ_УРОК:

                            цУровень = УРОВЕНЬ_РАСПИС;
                            Расписания.deleteAll();
                            Расписания.setTitle( "Расписания" );
                            Расписания.append( "..", null );
                            main.Логгер.info( "[SchedulesForm.java]: \\Меню\\Расписания\\" );

                            for ( Enumeration e = МенеджерРасписаний.Расписания.elements(); e.hasMoreElements(); ) {

                                Расписание = ( Schedules.ScheduleClass ) e.nextElement();
                                Расписания.append( Расписание.Название, ИконкаПапки );

                            }

                            цРасписание = МенеджерРасписаний.цВыбранныйЭлемент;
                            Расписания.setSelectedIndex( цРасписание + 1, true );
                            break;

                        case УРОВЕНЬ_ЗВОНОК:

                            цУровень = УРОВЕНЬ_УРОК;
                            Расписания.deleteAll();
                            Расписание = ( Schedules.ScheduleClass ) МенеджерРасписаний.Расписания.elementAt( цРасписание );
                            main.Логгер.info( "[SchedulesForm.java]: \\Меню\\Расписания\\"
                                    + Расписание.Название + "\\" );
                            Расписания.setTitle( Расписание.Название );
                            Расписания.append( "..", null );

                            for ( Enumeration e = Расписание.Уроки.elements(); e.hasMoreElements(); ) {

                                Урок = ( Schedules.LessonClass ) e.nextElement();
                                Расписания.append( Урок.Название, ИконкаПапки );

                            }
                            Расписания.setSelectedIndex( цУрок + 1, true );
                            break;
                    }

                } else {

                    switch ( цУровень ) {

                        case УРОВЕНЬ_РАСПИС:

                            Расписания.deleteAll();
                            цРасписание = ii - 1;
                            Расписание = ( Schedules.ScheduleClass ) МенеджерРасписаний.Расписания.elementAt( цРасписание );
                            main.Логгер.info( "[SchedulesForm.java]: \\Меню\\Расписания\\"
                                    + Расписание.Название + "\\" );

                            МенеджерРасписаний.цВыбранныйЭлемент = цРасписание;
                            Расписания.setTitle( Расписание.Название );
                            Расписания.append( "..", null );

                            for ( Enumeration e = Расписание.Уроки.elements(); e.hasMoreElements(); ) {

                                Урок = ( Schedules.LessonClass ) e.nextElement();
                                Расписания.append( Урок.Название, ИконкаПапки );
                            }

                            цУровень = УРОВЕНЬ_УРОК;
                            break;

                        case УРОВЕНЬ_УРОК:

                            Расписания.deleteAll();
                            цУрок = ii - 1;
                            Расписание = ( Schedules.ScheduleClass ) МенеджерРасписаний.Расписания.elementAt( цРасписание );
                            Урок = ( Schedules.LessonClass ) Расписание.Уроки.elementAt( цУрок );

                            main.Логгер.info( "[SchedulesForm.java]: \\Меню\\Расписания\\"
                                    + Расписание.Название + "\\" + Урок.Название + "\\" );

                            Расписания.setTitle( Расписание.Название + "\\" + Урок.Название );
                            Расписание.цВыбранныйЭлемент = цУрок;
                            Расписания.append( "..", null );

                            for ( Enumeration e = Урок.Звонки.elements(); e.hasMoreElements(); ) {

                                Звонок = ( Schedules.BellClass ) e.nextElement();
                                Текст = "" + ( ( Звонок.цЧасы < 10 ) ? "0" : "" ) + Звонок.цЧасы +
                                    "." + ( ( Звонок.цМинуты < 10 ) ? "0" : "" ) + Звонок.цМинуты;
                                Расписания.append( Текст, ИконкаФайла );

                            }

                            цУровень = УРОВЕНЬ_ЗВОНОК;
                            break;

                        case УРОВЕНЬ_ЗВОНОК:

                            цЗвонок = ii - 1;
                            Расписание = ( Schedules.ScheduleClass ) МенеджерРасписаний.Расписания.elementAt( цРасписание );
                            Урок = ( Schedules.LessonClass ) Расписание.Уроки.elementAt( цУрок );
                            Звонок = ( Schedules.BellClass ) Урок.Звонки.elementAt( цЗвонок );
                            Урок.цВыбранныйЭлемент = цЗвонок;

                            EditBellForm ФормаЗвонка = new EditBellForm( Мидлет, this, МенеджерРасписаний, Настройки, цРасписание, цУрок, цЗвонок );
                            ФормаЗвонка.Отобразить();
                            break;

                    }

                }


            // Добавить элемент.
            } else if ( команда == КомандаДобавить ) {

                main.Логгер.info( "[SchedulesForm.java]: <Добавить>" );

                switch ( цУровень ) {

                    case УРОВЕНЬ_РАСПИС:

                        EditScheduleForm ФормаРасписания = new EditScheduleForm( Мидлет, this, МенеджерРасписаний );
                        ФормаРасписания.Отобразить();
                        break;

                    case УРОВЕНЬ_УРОК:

                        EditLessonForm ФормаУрока = new EditLessonForm( Мидлет, this, МенеджерРасписаний, цРасписание );
                        ФормаУрока.Отобразить();
                        break;

                    case УРОВЕНЬ_ЗВОНОК:

                        EditBellForm ФормаЗвонка = new EditBellForm( Мидлет, this, МенеджерРасписаний, Настройки, цРасписание, цУрок );
                        ФормаЗвонка.Отобразить();
                        break;
                }


            // Изменить элемент.
            } else if ( команда == КомандаИзменить ) {

                main.Логгер.info( "[SchedulesForm.java]: <Изменить>" );

                switch ( цУровень ) {

                    case УРОВЕНЬ_РАСПИС:

                        EditScheduleForm ФормаРасписания = new EditScheduleForm( Мидлет, this, МенеджерРасписаний, Расписания.getSelectedIndex() - 1 );
                        ФормаРасписания.Отобразить();
                        break;

                    case УРОВЕНЬ_УРОК:

                        EditLessonForm ФормаУрока = new EditLessonForm( Мидлет, this, МенеджерРасписаний, цРасписание, Расписания.getSelectedIndex() - 1 );
                        ФормаУрока.Отобразить();
                        break;

                    case УРОВЕНЬ_ЗВОНОК:

                        EditBellForm ФормаЗвонка = new EditBellForm( Мидлет, this, МенеджерРасписаний, Настройки, цРасписание, цУрок, Расписания.getSelectedIndex() - 1 );
                        ФормаЗвонка.Отобразить();
                        break;
                }

            // Удалить элемент.
            } else if ( команда == КомандаУдалить ) {

                main.Логгер.info( "[SchedulesForm.java]: <Удалить>" );

                switch ( цУровень ) {

                    case УРОВЕНЬ_РАСПИС:

                        МенеджерРасписаний.УдалитьРасписание( Расписания.getSelectedIndex() - 1 );
                        break;

                    case УРОВЕНЬ_УРОК:

                        МенеджерРасписаний.УдалитьУрок( цРасписание, Расписания.getSelectedIndex() - 1 );
                        break;

                    case УРОВЕНЬ_ЗВОНОК:

                        МенеджерРасписаний.УдалитьЗвонок( цРасписание, цУрок, Расписания.getSelectedIndex() - 1 );
                        break;
                }

                Обновить();

            // Очистить элемент.
            } else if ( команда == КомандаОчистить ) {

                main.Логгер.info( "[SchedulesForm.java]: <Очистить>" );

                switch ( цУровень ) {

                    case УРОВЕНЬ_РАСПИС:

                        МенеджерРасписаний.Очистить();
                        break;

                    case УРОВЕНЬ_УРОК:

                        Расписание = ( Schedules.ScheduleClass ) МенеджерРасписаний.Расписания.elementAt( цРасписание );
                        Расписание.Очистить();
                        break;

                    case УРОВЕНЬ_ЗВОНОК:

                        Расписание = ( Schedules.ScheduleClass ) МенеджерРасписаний.Расписания.elementAt( цРасписание );
                        Урок = ( Schedules.LessonClass ) Расписание.Уроки.elementAt( цУрок );
                        Урок.Очистить();
                        break;
                }

                Обновить();


            } else if ( команда == КомандаСохранить ) {

                main.Логгер.info( "[SchedulesForm.java]: <Сохранить>" );
                МенеджерРасписаний.СохранитьРасписания( Настройки.Расписания.ИмяЗаписиВХранилище );
                Текст = "Расписания сохранены в хранилище.";
                Дисплей.setCurrent( new Alert( "Сообщение", Текст, null , AlertType.INFO ) );


            // Сохранение расписаний в RMS.
            } else if ( команда == КомандаСохранитьКак ) {

                main.Логгер.info( "[SchedulesForm.java]: <СохранитьКак>" );
                ( new SchedulesSaveAsForm( Мидлет, Расписания, Настройки, МенеджерРасписаний ) ).Отобразить();


            // Загрузка расписаний из RMS.
            } else if ( команда == КомандаЗагрузить ) {

                main.Логгер.info( "[SchedulesForm.java]: <Загрузить>" );
                ( new SchedulesLoadForm( Мидлет, this, Настройки, МенеджерРасписаний ) ).Отобразить();


            // Импорт расписаний.
            } else if ( команда == КомандаИмпорт ) {

                main.Логгер.info( "[SchedulesForm.java]: <Импорт>" );
                ( new SchelulesImportForm( Мидлет, this, Настройки, МенеджерРасписаний ) ).Отобразить();


            // Экспорт расписаний.
            } else if ( команда == КомандаЭкспорт ) {

                main.Логгер.info( "[SchedulesForm.java]: <Экспорт>" );
                ( new SchelulesExportForm( Мидлет, Расписания, Настройки, МенеджерРасписаний ) ).Отобразить();
            }

        }

    }

    // </editor-fold>

}
