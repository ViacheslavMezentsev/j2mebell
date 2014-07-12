
// <editor-fold defaultstate="collapsed" desc=" Подключаемые модули ">

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import java.util.*;

// </editor-fold>

public class EditBellForm implements CommandListener, ItemStateListener {

    // <editor-fold defaultstate="collapsed" desc=" Поля класса ">

    static final public byte РЕЖИМ_ДОБАВИТЬ = 0;
    static final public byte РЕЖИМ_ИЗМЕНИТЬ = 1;
    static final public byte РЕЖИМ_ВСТАВИТЬ = 2;

    private byte цРежим;
    private int цРасписание;
    private int цУрок;
    private int цЗвонок;

    public Form Форма;

    private ChoiceGroup ВыборРасписания;
    private ChoiceGroup ВыборУрока;
    private ChoiceGroup ВыборЗвонка;
    private TextField ПолеЧасы;
    private TextField ПолеМинуты;
    private ChoiceGroup ПолеТип;
    private ChoiceGroup ПолеМелодияОснов;
    private ChoiceGroup ПолеМелодияПредв;

    private MIDlet Мидлет;
    private Display Дисплей;
    private SchedulesForm РодительскоеОкно;
    private Schedules МенеджерРасписаний;
    private Settings Настройки;

    private Command КомандаПрименить = new Command( "Применить", Command.OK, 0 );
    private Command КомандаНазад = new Command( "Назад", Command.CANCEL, 1 );

    // </editor-fold>

    // <editor-fold desc=" Конструктор ">

    // Конструктор для создания новой записи.
    public EditBellForm( MIDlet мидлет, SchedulesForm родитель, Schedules менеджер,
            Settings Настройки, int цРасписание, int цУрок ) {

        Schedules.ScheduleClass Расписание;
        Schedules.LessonClass Урок;

        Мидлет = мидлет;
        Дисплей = Display.getDisplay( Мидлет );
        РодительскоеОкно = родитель;
        МенеджерРасписаний = менеджер;
        this.цРасписание = цРасписание;
        this.Настройки = Настройки;

        цРежим = РЕЖИМ_ДОБАВИТЬ;

        Форма = new Form ( "Добавить" );
        ВыборРасписания = new ChoiceGroup( "Расписание: ", Choice.POPUP );

        for ( Enumeration e = МенеджерРасписаний.Расписания.elements(); e.hasMoreElements(); ) {

            Расписание = ( Schedules.ScheduleClass ) e.nextElement();
            ВыборРасписания.append( Расписание.Название, null );
        }

        ВыборРасписания.setSelectedIndex( цРасписание, true );
        Форма.append( ВыборРасписания );

        Расписание = ( Schedules.ScheduleClass ) МенеджерРасписаний.Расписания.elementAt( цРасписание );
        ВыборУрока = new ChoiceGroup( "Урок: ", Choice.POPUP );

        for ( Enumeration e = Расписание.Уроки.elements(); e.hasMoreElements(); ) {

            Урок = ( Schedules.LessonClass ) e.nextElement();
            ВыборУрока.append( Урок.Название, null );
        }

        ВыборУрока.setSelectedIndex( цУрок, true );
        Форма.append( ВыборУрока );

        ПолеЧасы = new TextField( "Часы: ", "0", 4, TextField.NUMERIC );
        ПолеМинуты = new TextField( "Минуты: ", "0", 4, TextField.NUMERIC );
        ПолеТип = new ChoiceGroup( "Тип: ", Choice.POPUP,
                new String[]{ "начало", "конец", "свободный" }, null );
        ПолеТип.setSelectedIndex( 0, true );
//        ПолеМелодияОснов = new TextField( "МелодияОснов: ", "", 255, TextField.ANY );
//        ПолеМелодияПредв = new TextField( "МелодияПредв: ", "", 255, TextField.ANY );
        ПолеМелодияОснов = new ChoiceGroup( "МелодияОснов: ", Choice.POPUP );
        ПолеМелодияОснов.append( "пусто", null );
        ПолеМелодияОснов.append( "Обзор...", null );
        ПолеМелодияОснов.setSelectedIndex( 0, true );

        ПолеМелодияПредв = new ChoiceGroup( "МелодияПредв: ", Choice.POPUP );
        ПолеМелодияПредв.append( "пусто", null );
        ПолеМелодияПредв.append( "Обзор...", null );
        ПолеМелодияПредв.setSelectedIndex( 0, true );

        // Разделитель.
        Форма.append( new Spacer ( 3, 10 ) );

        Форма.append( "[ Параметры звонка ]" );
        Форма.append( ПолеЧасы );
        Форма.append( ПолеМинуты );
        Форма.append( ПолеТип );
        Форма.append( ПолеМелодияОснов );
        Форма.append( ПолеМелодияПредв );

        Форма.addCommand( КомандаПрименить );
        Форма.addCommand( КомандаНазад );

        Форма.setCommandListener( this );
        Форма.setItemStateListener( this );

        main.Логгер.info( "[EditBellForm.java]: EditBellForm()" );
    }

    // Конструктор для редактирования текущей записи.
    public EditBellForm( MIDlet мидлет, SchedulesForm родитель, Schedules менеджер,
            Settings Настройки, int цРасписание, int цУрок, int цЗвонок ) {

        Мидлет = мидлет;
        Дисплей = Display.getDisplay( Мидлет );
        РодительскоеОкно = родитель;
        МенеджерРасписаний = менеджер;
        this.Настройки = Настройки;

        this.цРасписание = цРасписание;
        this.цУрок = цУрок;
        this.цЗвонок = цЗвонок;

        Schedules.ScheduleClass Расписание;
        Schedules.LessonClass Урок;
        Schedules.BellClass Звонок;

        String Текст;

        Расписание = ( Schedules.ScheduleClass ) МенеджерРасписаний.Расписания.elementAt( цРасписание );
        Урок = ( Schedules.LessonClass ) Расписание.Уроки.elementAt( цУрок );
        Звонок = ( Schedules.BellClass ) Урок.Звонки.elementAt( цЗвонок );

        цРежим = РЕЖИМ_ИЗМЕНИТЬ;
        Форма = new Form ( "Изменить звонок" );

        ВыборРасписания = new ChoiceGroup( "Расписание: ", Choice.POPUP );

        for ( Enumeration e = МенеджерРасписаний.Расписания.elements(); e.hasMoreElements(); ) {

            Расписание = ( Schedules.ScheduleClass ) e.nextElement();
            ВыборРасписания.append( Расписание.Название, null );
        }

        ВыборРасписания.setSelectedIndex( цРасписание, true );
        Форма.append( ВыборРасписания );

        Расписание = ( Schedules.ScheduleClass ) МенеджерРасписаний.Расписания.elementAt( цРасписание );
        ВыборУрока = new ChoiceGroup( "Урок: ", Choice.POPUP );

        for ( Enumeration e = Расписание.Уроки.elements(); e.hasMoreElements(); ) {

            Урок = ( Schedules.LessonClass ) e.nextElement();
            ВыборУрока.append( Урок.Название, null );
        }

        ВыборУрока.setSelectedIndex( цУрок, true );
        Форма.append( ВыборУрока );

        Урок = ( Schedules.LessonClass ) Расписание.Уроки.elementAt( цУрок );
        ВыборЗвонка = new ChoiceGroup( "Звонок: ", Choice.POPUP );

        for ( Enumeration e = Урок.Звонки.elements(); e.hasMoreElements(); ) {

            Звонок = ( Schedules.BellClass ) e.nextElement();
            Текст = "" + ( ( Звонок.цЧасы < 10 ) ? "0" : "" ) + Звонок.цЧасы +
                "." + ( ( Звонок.цМинуты < 10 ) ? "0" : "" ) + Звонок.цМинуты;
            ВыборЗвонка.append( Текст, null );
        }

        ВыборЗвонка.setSelectedIndex( цЗвонок, true );
        Форма.append( ВыборЗвонка );

        Звонок = ( Schedules.BellClass ) Урок.Звонки.elementAt( цЗвонок );


        ПолеЧасы = new TextField( "Часы: ", Integer.toString( Звонок.цЧасы ), 4, TextField.NUMERIC );
        ПолеМинуты = new TextField( "Минуты: ", Integer.toString( Звонок.цМинуты ), 4, TextField.NUMERIC );
        ПолеТип = new ChoiceGroup( "Тип: ", Choice.POPUP,
                new String[]{ "начало", "конец", "свободный" }, null );

        switch ( Звонок.Тип ){

            case Schedules.ТИП_НАЧАЛО:

                ПолеТип.setSelectedIndex( 0, true );
                break;

            case Schedules.ТИП_КОНЕЦ:

                ПолеТип.setSelectedIndex( 1, true );
                break;

            case Schedules.ТИП_СВОБОДНЫЙ:

                ПолеТип.setSelectedIndex( 2, true );
                break;
        }
//        ПолеМелодияОснов = new TextField( "МелодияОснов: ", Звонок.Путь, 255, TextField.ANY );
//        ПолеМелодияПредв = new TextField( "МелодияПредв: ", Звонок.Путь2, 255, TextField.ANY );

        ПолеМелодияОснов = new ChoiceGroup( "МелодияОснов: ", Choice.POPUP );
        ПолеМелодияОснов.append( ( Звонок.Путь != null ) ? Звонок.Путь : "пусто", null );
        ПолеМелодияОснов.append( "Обзор...", null );
        ПолеМелодияОснов.setSelectedIndex( 0, true );

        ПолеМелодияПредв = new ChoiceGroup( "МелодияПредв: ", Choice.POPUP );
        ПолеМелодияПредв.append( ( Звонок.Путь2 != null ) ? Звонок.Путь2 : "пусто", null );
        ПолеМелодияПредв.append( "Обзор...", null );
        ПолеМелодияПредв.setSelectedIndex( 0, true );

        // Разделитель.
        Форма.append( new Spacer( 3, 10 ) );

        Форма.append( "[ Параметры звонка ]" );
        Форма.append( ПолеЧасы );
        Форма.append( ПолеМинуты );
        Форма.append( ПолеТип );
        Форма.append( ПолеМелодияОснов );
        Форма.append( ПолеМелодияПредв );

        Форма.addCommand( КомандаПрименить );
        Форма.addCommand( КомандаНазад );

        Форма.setCommandListener( this );
        Форма.setItemStateListener( this );

        Текст = "" + ( ( Звонок.цЧасы < 10 ) ? "0" : "" ) + Звонок.цЧасы +
            "." + ( ( Звонок.цМинуты < 10 ) ? "0" : "" ) + Звонок.цМинуты;

        main.Логгер.info( "[EditBellForm.java]: EditBellForm( "
                + Расписание.Название + ", "
                + Урок.Название + ", "
                + Текст + " )"
                );
    }

    // </editor-fold>

    // <editor-fold desc=" Методы класса ">

    public void Отобразить() {

        Дисплей.setCurrent( Форма );
    }

    public void Обновить() {

        String Текст;

        Schedules.ScheduleClass Расписание;
        Schedules.LessonClass Урок;
        Schedules.BellClass Звонок;

        // Очищаем все параметры.
        ВыборРасписания.deleteAll();
        ВыборУрока.deleteAll();
        ВыборЗвонка.deleteAll();
        ПолеЧасы.setString( "" );
        ПолеМинуты.setString( "" );
        ПолеТип.setSelectedIndex( 0, true );
        ПолеМелодияОснов.deleteAll();
        ПолеМелодияПредв.deleteAll();

        // Заполняем элементы формы.
        for ( Enumeration e = МенеджерРасписаний.Расписания.elements(); e.hasMoreElements(); ) {

            Расписание = ( Schedules.ScheduleClass ) e.nextElement();

            if ( Расписание.Уроки.size() > 0 ) {

                ВыборРасписания.append( Расписание.Название, null );
            }

        }

        if ( ВыборРасписания.size() == 0 ) return;

        ВыборРасписания.setSelectedIndex( цРасписание, true );

        Расписание = ( Schedules.ScheduleClass ) МенеджерРасписаний.Расписания.elementAt( цРасписание );

        for ( Enumeration e = Расписание.Уроки.elements(); e.hasMoreElements(); ) {

            Урок = ( Schedules.LessonClass ) e.nextElement();

            if ( Урок.Звонки.size() > 0 ) {

                ВыборУрока.append( Урок.Название, null );
            }

        }

        if ( ВыборУрока.size() == 0 ) return;

        ВыборУрока.setSelectedIndex( цУрок, true );

        Урок = ( Schedules.LessonClass ) Расписание.Уроки.elementAt( цУрок );

        for ( Enumeration e = Урок.Звонки.elements(); e.hasMoreElements(); ) {

            Звонок = ( Schedules.BellClass ) e.nextElement();

            Текст = "" + ( ( Звонок.цЧасы < 10 ) ? "0" : "" ) + Звонок.цЧасы +
                "." + ( ( Звонок.цМинуты < 10 ) ? "0" : "" ) + Звонок.цМинуты;

            ВыборЗвонка.append( Текст, null );
        }

        ВыборЗвонка.setSelectedIndex( цЗвонок, true );

        Звонок = ( Schedules.BellClass ) Урок.Звонки.elementAt( цЗвонок );

        ПолеЧасы.setString( Integer.toString( Звонок.цЧасы ) );
        ПолеМинуты.setString( Integer.toString( Звонок.цМинуты ) );

        switch ( Звонок.Тип ){

            case Schedules.ТИП_НАЧАЛО:

                ПолеТип.setSelectedIndex( 0, true );
                break;

            case Schedules.ТИП_КОНЕЦ:

                ПолеТип.setSelectedIndex( 1, true );
                break;

            case Schedules.ТИП_СВОБОДНЫЙ:

                ПолеТип.setSelectedIndex( 2, true );
                break;
        }

        ПолеМелодияОснов.append( ( Звонок.Путь != null ) ? Звонок.Путь : "пусто", null );
        ПолеМелодияОснов.append( "Обзор...", null );
        ПолеМелодияОснов.setSelectedIndex( 0, true );

        ПолеМелодияПредв.append( ( Звонок.Путь2 != null ) ? Звонок.Путь2 : "пусто", null );
        ПолеМелодияПредв.append( "Обзор...", null );
        ПолеМелодияПредв.setSelectedIndex( 0, true );
    }

    // </editor-fold>

    // <editor-fold desc=" Обработчики событий ">

    public void commandAction( Command команда, Displayable элемент ) {

        byte цТип;
        int цЧасы, цМинуты;
        String Текст;

        if ( элемент == Форма ) {

            if ( команда == КомандаПрименить ) {

                switch ( цРежим ) {

                    case РЕЖИМ_ДОБАВИТЬ:

                        цРасписание = ВыборРасписания.getSelectedIndex();
                        цУрок = ВыборУрока.getSelectedIndex();

                        цЧасы = Integer.parseInt( ПолеЧасы.getString() );
                        цМинуты = Integer.parseInt( ПолеМинуты.getString() );
                        цТип = ( byte ) ПолеТип.getSelectedIndex();

                        МенеджерРасписаний.ДобавитьЗвонок( цРасписание, цУрок,
                                цЧасы, цМинуты, цТип, ПолеМелодияОснов.getString(0), ПолеМелодияПредв.getString(0) );

                        РодительскоеОкно.Обновить();
                        Дисплей.setCurrent( РодительскоеОкно.Расписания );

                        Текст = "" + ( ( цЧасы < 10 ) ? "0" : "" ) + цЧасы +
                                "." + ( ( цМинуты < 10 ) ? "0" : "" ) + цМинуты;
                        Дисплей.setCurrent( new Alert( "Сообщение", "Звонок: " + Текст + " добавлен.", null, AlertType.INFO ) );
                        break;

                    case РЕЖИМ_ИЗМЕНИТЬ:
                        цРасписание = ВыборРасписания.getSelectedIndex();
                        цУрок = ВыборУрока.getSelectedIndex();
                        цЗвонок = ВыборЗвонка.getSelectedIndex();

                        цЧасы = Integer.parseInt( ПолеЧасы.getString() );
                        цМинуты = Integer.parseInt( ПолеМинуты.getString() );
                        цТип = ( byte ) ПолеТип.getSelectedIndex();

                        МенеджерРасписаний.ИзменитьЗвонок( цРасписание, цУрок, цЗвонок,
                                цЧасы, цМинуты, цТип, ПолеМелодияОснов.getString(0), ПолеМелодияПредв.getString(0) );

                        Текст = "" + ( ( цЧасы < 10 ) ? "0" : "" ) + цЧасы +
                                "." + ( ( цМинуты < 10 ) ? "0" : "" ) + цМинуты;
                        
                        Дисплей.setCurrent( new Alert( "Сообщение", "Звонок: " + Текст + " сохранён.", null, AlertType.INFO ) );

                        Обновить();
                        break;

                } // switch


            } else if ( команда == КомандаНазад ) {

                main.Логгер.info( "[EditBellForm.java]: <Назад>" );
                РодительскоеОкно.Обновить();
                Дисплей.setCurrent( РодительскоеОкно.Расписания );

            }

        }

    }

    public void itemStateChanged( Item элемент ) {

        if ( элемент == ВыборРасписания ) {

            цРасписание = ВыборРасписания.getSelectedIndex();
            цУрок = 0;
            цЗвонок = 0;
            Обновить();


        } else if ( элемент == ВыборУрока ) {

            цУрок = ВыборУрока.getSelectedIndex();
            цЗвонок = 0;
            Обновить();


        } else if ( элемент == ВыборЗвонка ) {

            цЗвонок = ВыборЗвонка.getSelectedIndex();
            Обновить();


        } else if ( элемент == ПолеМелодияОснов ) {

            switch ( ПолеМелодияОснов.getSelectedIndex() ) {

                case 0:

                    ( new EditSettingsStringForm( Мидлет, Форма, ПолеМелодияОснов ) ).Отобразить();
                    break;

                case 1:

                    ( new FileBrowserForm( Мидлет, Форма, Настройки, ПолеМелодияОснов ) ).Отобразить();
                    break;

            }


        } else if ( элемент == ПолеМелодияПредв ) {

            switch ( ПолеМелодияПредв.getSelectedIndex() ) {

                case 0:

                    ( new EditSettingsStringForm( Мидлет, Форма, ПолеМелодияПредв ) ).Отобразить();
                    break;

                case 1:

                    ( new FileBrowserForm( Мидлет, Форма, Настройки, ПолеМелодияПредв ) ).Отобразить();
                    break;

            }

        }

    }

    // </editor-fold>

}
