
// <editor-fold desc=" Подключаемые модули ">

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

// </editor-fold>

public class MenuForm implements CommandListener {

    // <editor-fold desc=" Поля класса ">

    static private String Title = "Меню";

    // Индексы элементов основного меню.
    static final public byte ЭЛЕМЕНТ_МОНИТОР = 0;
    static final public byte ЭЛЕМЕНТ_НАСТРОЙКИ = 1;
    static final public byte ЭЛЕМЕНТ_ПЛАНЫ = 2;
    static final public byte ЭЛЕМЕНТ_РАСПИСАНИЯ = 3;
    static final public byte ЭЛЕМЕНТ_ПРОВОДНИК = 4;
    static final public byte ЭЛЕМЕНТ_ОПРОГРАММЕ = 5;

    // Команды
    private Command КомандаВыйти = new Command( "Выйти", Command.EXIT, 0 );
    private Command КомандаТест = new Command( "Тест", Command.SCREEN, 1 );

    // Дисплей
    private MIDlet Мидлет;
    private Display Дисплей;
    private Schedules МенеджерРасписаний;
    private Settings Настройки;

    // Окна
    private List Меню;

    // </editor-fold>

    // <editor-fold desc=" Конструктор ">

    public MenuForm( MIDlet Мидлет, Settings Настройки ) {

        this.Мидлет = Мидлет;
        this.Настройки = Настройки;

        Дисплей = Display.getDisplay( Мидлет );

        main.Логгер.info( "----------------------------------------" );
        main.Логгер.info( "[MenuForm.java]: MenuForm()" );

        if ( main.РЕЖИМ_СИМУЛЯТОРА == true ) Title += " [симулятор]";

        // Создаём окно основного меню.
        Меню = new List( Title, List.IMPLICIT );

        // Добавляем элемнты списка меню.
        String[] ЭлементыМеню = {

            "Монитор",
            "Настройки",
            "Планы",
            "Расписания",
            "Проводник",
            "О программе"
        };

        for ( int ii = 0; ii < ЭлементыМеню.length; ii++ ) {

            Меню.append( ЭлементыМеню[ii], null );
        }

        // Добавляем команды окна.
        Меню.addCommand( КомандаВыйти );

        if ( main.РЕЖИМ_СИМУЛЯТОРА == true ) {

            Меню.addCommand( КомандаТест );
        }

        // Установка обработчика событий Формы.
        Меню.setCommandListener( this );

        // Создаём один экземпляр объектов:
        МенеджерРасписаний = new Schedules( Мидлет, Настройки );

        // Пытаемся загрузить расписания из хранилища.
        МенеджерРасписаний.ЗагрузитьРасписания( Настройки.Расписания.ИмяЗаписиВХранилище );

        if ( МенеджерРасписаний.Расписания.isEmpty()
                && МенеджерРасписаний.СписокПланов.Элементы.isEmpty() ) {

            МенеджерРасписаний.НачальныеНастройки();
        }

        main.Логгер.info( "[MenuForm.java]: Старт программы. Версия: " + Мидлет.getAppProperty( "MIDlet-Version" ) );
    }

    // </editor-fold>

    // <editor-fold desc=" Методы класса ">

    public void Отобразить() {

        Дисплей.setCurrent( Меню );
    }

    // </editor-fold>

    // <editor-fold desc=" Обработчики событий ">

    // Обработчик команд.
    public void commandAction( Command команда, Displayable элемент ) {

        // Работаем с Главным меню.
        if ( элемент == Меню ) {

            // Обработка элементов списка.
            if ( команда == List.SELECT_COMMAND ) {

                main.Логгер.info( "[MenuForm.java]: -> \\Меню\\" + Меню.getString( Меню.getSelectedIndex() ) + "\\" );

                switch ( Меню.getSelectedIndex() ) {

                    case ЭЛЕМЕНТ_МОНИТОР:

                        if ( Настройки.Расписания.ТекущееРасписание == -1 ) return;
                        ( new MonitorForm( Мидлет, Меню, МенеджерРасписаний, Настройки ) ).Отобразить();
                        break;

                    case ЭЛЕМЕНТ_НАСТРОЙКИ:

                        ( new SettingsForm( Мидлет, Меню, МенеджерРасписаний, Настройки ) ).Отобразить();
                        break;

                    case ЭЛЕМЕНТ_ПЛАНЫ:

                        ( new PlansForm( Мидлет, Меню, МенеджерРасписаний, Настройки  ) ).Отобразить();
                        break;

                    case ЭЛЕМЕНТ_РАСПИСАНИЯ:

                        ( new SchedulesForm( Мидлет, Меню, МенеджерРасписаний, Настройки ) ).Отобразить();
                        break;

                    case ЭЛЕМЕНТ_ПРОВОДНИК:

                        ( new FileBrowserForm( Мидлет, Меню, Настройки ) ).Отобразить();
                        break;

                    case ЭЛЕМЕНТ_ОПРОГРАММЕ:

                        ( new AboutForm( Мидлет, Меню, МенеджерРасписаний, Настройки ) ).Отобразить();
                        break;
                }

            // Выход из программы.
            } else if ( команда == КомандаВыйти ) {

                main.Логгер.info( "[MenuForm.java]: <Выйти>" );
                main.Логгер.info( "[MenuForm.java]: Выход из программы." );
                Мидлет.notifyDestroyed();

            // Тест.
            } else if ( команда == КомандаТест ) { }

        }

    }

    // </editor-fold>

}
