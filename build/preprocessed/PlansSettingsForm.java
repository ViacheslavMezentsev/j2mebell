
// <editor-fold defaultstate="collapsed" desc=" Подключаемые модули ">

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

import java.util.*;

// </editor-fold>

public class PlansSettingsForm implements CommandListener {

    // <editor-fold defaultstate="collapsed" desc=" Поля класса ">

    private Command КомандаНазад;
    private Command КомандаПрименить;

    private Form Форма;

    private ChoiceGroup ВыборПлана;
    private TextField ПолеВысотаЯчейки;
    private TextField ПолеШиринаЯчейки;

    private MIDlet Мидлет;
    private Display Дисплей;
    private Displayable РодительскоеОкно;
    private Schedules МенеджерРасписаний;
    private Settings Настройки;

    // </editor-fold>

    // <editor-fold desc=" Конструктор ">

    public PlansSettingsForm( MIDlet Мидлет, Displayable Окно, Schedules Менеджер, Settings Настройки ) {

        Schedules.ОбразПлана План;

        this.Мидлет = Мидлет;
        Дисплей = Display.getDisplay( Мидлет );
        МенеджерРасписаний = Менеджер;
        РодительскоеОкно = Окно;
        this.Настройки = Настройки;

        Форма = new Form ( "Настройки\\Планы" );

        ВыборПлана = new ChoiceGroup( "План: ", Choice.POPUP );

        for ( Enumeration e = МенеджерРасписаний.СписокПланов.Элементы.elements(); e.hasMoreElements(); ) {

            План = ( Schedules.ОбразПлана ) e.nextElement();
            ВыборПлана.append( План.Название, null );
        }

        if ( ВыборПлана.size() > 0 ) {

            ВыборПлана.setSelectedIndex( Настройки.Расписания.ТекущийПлан, true );

        } else {

            ВыборПлана.append( "пусто", null );
        }

        ПолеВысотаЯчейки = new TextField( "Высота ячейки: ",
                Integer.toString( Настройки.Планировщик.ВысотаЯчейки ), 4, TextField.NUMERIC );
        ПолеШиринаЯчейки = new TextField( "Ширина ячейки: ",
                Integer.toString( Настройки.Планировщик.ШиринаЯчейки ), 4, TextField.NUMERIC );

        Форма.append( ВыборПлана );

        // Разделитель.
        Форма.append( new Spacer ( 3, 10 ) );
        Форма.append( "[ Планировщик ]" );
        Форма.append( ПолеВысотаЯчейки );
        Форма.append( ПолеШиринаЯчейки );

        // Создаём команды.
        КомандаНазад = new Command( "Назад", Command.BACK, 0 );
        КомандаПрименить = new Command( "Применить", Command.SCREEN, 1 );

        Форма.addCommand( КомандаНазад );
        Форма.addCommand( КомандаПрименить );

        // Установка обработчика событий Формы.
        Форма.setCommandListener( this );

        main.Логгер.info( "[PlansSettingsForm.java]: PlansSettingsForm()" );
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

                main.Логгер.info( "[PlansSettingsForm.java]: <Назад>" );
                Дисплей.setCurrent( РодительскоеОкно );


            } else if ( команда == КомандаПрименить ) {

                main.Логгер.info( "[PlansSettingsForm.java]: <Применить>" );

                Настройки.Расписания.ТекущийПлан = ВыборПлана.getSelectedIndex();
                Настройки.Планировщик.ВысотаЯчейки = Integer.parseInt( ПолеВысотаЯчейки.getString() );
                Настройки.Планировщик.ШиринаЯчейки = Integer.parseInt( ПолеШиринаЯчейки.getString() );

                Дисплей.setCurrent( new Alert( "Сообщение", "Настройки планов сохранены.", null, AlertType.INFO ) );
            }

        }

    }

    // </editor-fold>
    
}

