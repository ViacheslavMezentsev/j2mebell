
// <editor-fold defaultstate="collapsed" desc=" Подключаемые модули ">

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

// </editor-fold>

public class BellsSettingsForm implements CommandListener {

    // <editor-fold defaultstate="collapsed" desc=" Поля класса ">

    private Command КомандаНазад;
    private Command КомандаПрименить;

    private Form Форма;

    private ChoiceGroup ПолеПредваритЗвонков;
    private TextField ПолеДлительностьОсновных;
    private TextField ПолеДлительностьПредварит;
    private TextField ПолеВремяДоНачала;
    private TextField ПолеВремяДоОкончания;

    private MIDlet Мидлет;
    private Display Дисплей;
    private Displayable РодительскоеОкно;
    private Schedules МенеджерРасписаний;
    private Settings Настройки;

    // </editor-fold>

    // <editor-fold desc=" Конструктор ">

    public BellsSettingsForm( MIDlet Мидлет, Displayable Окно, Schedules Менеджер, Settings Настройки ) {

        this.Мидлет = Мидлет;
        Дисплей = Display.getDisplay( Мидлет );
        МенеджерРасписаний = Менеджер;
        РодительскоеОкно = Окно;
        this.Настройки = Настройки;

        Форма = new Form ( "Настройки\\Звонки" );

        String Элементы[] = new String[]{ "началом", "окончанием" };

        ПолеПредваритЗвонков = new ChoiceGroup( "Предварительный перед: ", Choice.MULTIPLE, Элементы, null );
        ПолеПредваритЗвонков.setSelectedIndex( 0, Настройки.Звонки.логПредваритПередНачал );
        ПолеПредваритЗвонков.setSelectedIndex( 1, Настройки.Звонки.логПредваритПередОконч );

        ПолеДлительностьОсновных = new TextField( "Основных (сек): ",
                Integer.toString( Настройки.Звонки.ДлительностьОсновных ), 4, TextField.NUMERIC );
        ПолеДлительностьПредварит = new TextField( "Предварит. (сек): ",
                Integer.toString( Настройки.Звонки.ДлительностьПредварит ), 4, TextField.NUMERIC );
        ПолеВремяДоНачала = new TextField( "Время до начала (мин): ",
                Integer.toString( Настройки.Звонки.ВремяДоНачала ), 4, TextField.NUMERIC );
        ПолеВремяДоОкончания = new TextField( "Время до окончания (мин): ",
                Integer.toString( Настройки.Звонки.ВремяДоОкончания ), 4, TextField.NUMERIC );

        Форма.append( ПолеПредваритЗвонков );

        // Разделитель.
        Форма.append( new Spacer ( 3, 10 ) );
        Форма.append( "[ Длительности ]" );
        Форма.append( ПолеДлительностьОсновных );
        Форма.append( ПолеДлительностьПредварит );

        // Разделитель.
        Форма.append( new Spacer ( 3, 10 ) );
        Форма.append( "[ Предварительные ]" );
        Форма.append( ПолеВремяДоНачала );
        Форма.append( ПолеВремяДоОкончания );

        // Создаём команды.
        КомандаНазад = new Command( "Назад", Command.BACK, 0 );
        КомандаПрименить = new Command( "Применить", Command.SCREEN, 1 );

        Форма.addCommand( КомандаНазад );
        Форма.addCommand( КомандаПрименить );

        // Установка обработчика событий Формы.
        Форма.setCommandListener( this );

        main.Логгер.info( "[BellsSettingsForm.java]: BellsSettingsForm()" );
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

                main.Логгер.info( "[BellsSettingsForm.java]: <Назад>" );
                Дисплей.setCurrent( РодительскоеОкно );


            } else if ( команда == КомандаПрименить ) {

                main.Логгер.info( "[BellsSettingsForm.java]: <Применить>" );

                Настройки.Звонки.логПредваритПередНачал = ПолеПредваритЗвонков.isSelected( 0 );
                Настройки.Звонки.логПредваритПередОконч = ПолеПредваритЗвонков.isSelected( 1 );
                Настройки.Звонки.ДлительностьОсновных = Integer.parseInt( ПолеДлительностьОсновных.getString() );
                Настройки.Звонки.ДлительностьПредварит = Integer.parseInt( ПолеДлительностьПредварит.getString() );
                Настройки.Звонки.ВремяДоНачала = Integer.parseInt( ПолеВремяДоНачала.getString() );
                Настройки.Звонки.ВремяДоОкончания = Integer.parseInt( ПолеВремяДоОкончания.getString() );

                Дисплей.setCurrent( new Alert( "Сообщение", "Настройки звонков сохранены.", null, AlertType.INFO ) );

            }

        }

    }

    // </editor-fold>

}
