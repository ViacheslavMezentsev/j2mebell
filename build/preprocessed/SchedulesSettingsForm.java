
// <editor-fold defaultstate="collapsed" desc=" Подключаемые модули ">

import java.util.*;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

// </editor-fold>

public class SchedulesSettingsForm implements CommandListener {

    // <editor-fold defaultstate="collapsed" desc=" Поля класса ">

    private Command КомандаНазад;
    private Command КомандаПрименить;

    private Form Форма;

    private ChoiceGroup ПолеВыбораРежима;
    private ChoiceGroup ВыборРасписания;
    private TextField ПолеИмяЗаписи;

    private MIDlet Мидлет;
    private Display Дисплей;
    private Displayable РодительскоеОкно;
    private Schedules МенеджерРасписаний;
    private Settings Настройки;

    // </editor-fold>

    // <editor-fold desc=" Конструктор ">

    public SchedulesSettingsForm( MIDlet Мидлет, Displayable Окно, Schedules Менеджер, Settings Настройки ) {

        Schedules.ScheduleClass Расписание;

        this.Мидлет = Мидлет;
        Дисплей = Display.getDisplay( Мидлет );
        МенеджерРасписаний = Менеджер;
        РодительскоеОкно = Окно;
        this.Настройки = Настройки;

        Форма = new Form ( "Настройки\\Расписания" );

        String Элементы[] = new String[]{ "суточный", "годовой план" };
        ПолеВыбораРежима = new ChoiceGroup( "Режим: ", Choice.EXCLUSIVE, Элементы, null );
        ПолеВыбораРежима.setSelectedIndex( Настройки.Расписания.Режим, true );

        ВыборРасписания = new ChoiceGroup( "Расписание: ", Choice.POPUP );

        for ( Enumeration e = МенеджерРасписаний.Расписания.elements(); e.hasMoreElements(); ) {

            Расписание = ( Schedules.ScheduleClass ) e.nextElement();
            ВыборРасписания.append( Расписание.Название, null );
        }

        if ( ВыборРасписания.size() > 0 ) {

            ВыборРасписания.setSelectedIndex( Настройки.Расписания.ТекущееРасписание, true );

        } else {

            ВыборРасписания.append( "пусто", null );
        }

        ПолеИмяЗаписи = new TextField( "Имя записи в хранилище: ", Настройки.Расписания.ИмяЗаписиВХранилище, 16, TextField.ANY );

        Форма.append( ПолеВыбораРежима );
        Форма.append( ВыборРасписания );
        Форма.append( ПолеИмяЗаписи );

        // Создаём команды.
        КомандаНазад = new Command( "Назад", Command.BACK, 0 );
        КомандаПрименить = new Command( "Применить", Command.SCREEN, 1 );

        Форма.addCommand( КомандаНазад );
        Форма.addCommand( КомандаПрименить );

        // Установка обработчика событий Формы.
        Форма.setCommandListener( this );

        main.Логгер.info( "[SchedulesSettingsForm.java]: SchedulesSettingsForm()" );
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

                main.Логгер.info( "[SchedulesSettingsForm.java]: <Назад>" );
                Дисплей.setCurrent( РодительскоеОкно );


            } else if ( команда == КомандаПрименить ) {

                main.Логгер.info( "[SchedulesSettingsForm.java]: <Применить>" );
                Настройки.Расписания.Режим = ПолеВыбораРежима.getSelectedIndex();
                Настройки.Расписания.ТекущееРасписание = ВыборРасписания.getSelectedIndex();
                Настройки.Расписания.ИмяЗаписиВХранилище = ПолеИмяЗаписи.getString();

                Дисплей.setCurrent( new Alert( "Сообщение", "Настройки расписаний сохранены.", null, AlertType.INFO ) );
            }

        }

    }

    // </editor-fold>
    
}

