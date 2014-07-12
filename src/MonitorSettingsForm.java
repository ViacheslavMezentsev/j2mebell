
// <editor-fold defaultstate="collapsed" desc=" Подключаемые модули ">

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

// </editor-fold>

public class MonitorSettingsForm implements CommandListener, ItemStateListener {

    // <editor-fold defaultstate="collapsed" desc=" Поля класса ">

    private Command КомандаНазад;
    private Command КомандаПрименить;

    private Form Форма;

    private MIDlet Мидлет;
    private Display Дисплей;
    private Displayable РодительскоеОкно;
    private Settings Настройки;

    private TextField ПолеШиринаЭкрана;
    private TextField ПолеВысотаЭрана;
    private TextField ПолеОтступXY;
    private TextField ПолеШагСеткиXY;

    private TextField ПолеДиаграммаОтступСверху;
    private TextField ПолеДиаграммаТолщинаПолосы;
    private TextField ПолеДиаграммаДлинаИнтервала;

    private TextField ПолеВремяОтступСверху;
    private TextField ПолеПланОтступСверху;
    private TextField ПолеРежимОтступСверху;

    private ChoiceGroup ПолеПолныйЭкран;
    private ChoiceGroup ПолеЭлементы;

    // </editor-fold>

    // <editor-fold desc=" Конструктор ">

    public MonitorSettingsForm( MIDlet Мидлет, Displayable Окно, Settings Настройки ) {

        String Элементы[] = null;

        this.Мидлет = Мидлет;
        Дисплей = Display.getDisplay( Мидлет );
        РодительскоеОкно = Окно;
        this.Настройки = Настройки;

        Форма = new Form ( "Настройки\\Монитор" );

        // Создаём команды.
        КомандаНазад = new Command( "Назад", Command.BACK, 0 );
        КомандаПрименить = new Command( "Применить", Command.SCREEN, 1 );

        ПолеШиринаЭкрана = new TextField( "Ширина экрана: ",
                Integer.toString( Настройки.Монитор.ШиринаЭкрана ), 4, TextField.NUMERIC );
        ПолеВысотаЭрана = new TextField( "Высота экрана: ",
                Integer.toString( Настройки.Монитор.ВысотаЭкрана ), 4, TextField.NUMERIC );
        ПолеОтступXY = new TextField( "Отступ XY: ",
                Integer.toString( Настройки.Монитор.ОтступXY ), 4, TextField.NUMERIC );
        ПолеШагСеткиXY = new TextField( "Шаг сетки XY: ",
                Integer.toString( Настройки.Монитор.ШагСеткиXY ), 4, TextField.NUMERIC );

        Элементы = new String[]{ "Полный экран" };
        ПолеПолныйЭкран = new ChoiceGroup( "", Choice.MULTIPLE, Элементы, null );
        ПолеПолныйЭкран.setSelectedIndex( 0, Настройки.Монитор.ПолныйЭкран );

        ПолеДиаграммаОтступСверху = new TextField( "Отступ сверху: ",
                Integer.toString( Настройки.Монитор.ДиаграммаОтступСверху ), 4, TextField.NUMERIC );
        ПолеДиаграммаТолщинаПолосы = new TextField( "Толщина полосы: ",
                Integer.toString( Настройки.Монитор.ДиаграммаТолщинаПолосы ), 4, TextField.NUMERIC );
        ПолеДиаграммаДлинаИнтервала = new TextField( "Длина интервала (мин): ",
                Integer.toString( Настройки.Монитор.ДиаграммаДлинаИнтервала ), 4, TextField.NUMERIC );

        ПолеВремяОтступСверху = new TextField( "Отступ сверху: ",
                Integer.toString( Настройки.Монитор.ВремяОтступСверху ), 4, TextField.NUMERIC );
        ПолеПланОтступСверху = new TextField( "Отступ сверху: ",
                Integer.toString( Настройки.Монитор.ПланОтступСверху ), 4, TextField.NUMERIC );
        ПолеРежимОтступСверху = new TextField( "Отступ сверху: ",
                Integer.toString( Настройки.Монитор.РежимОтступСверху ), 4, TextField.NUMERIC );


        Элементы = new String[]{ "Сетку", "Рамку", "Дату", "Время", "План", "Режим", "Диаграмму", "Легенду" };
        ПолеЭлементы = new ChoiceGroup( "Показывать: ", Choice.MULTIPLE, Элементы, null );

        ПолеЭлементы.setSelectedIndex( 0, Настройки.Монитор.логПоказатьСетку );
        ПолеЭлементы.setSelectedIndex( 1, Настройки.Монитор.логПоказатьРамку );
        ПолеЭлементы.setSelectedIndex( 2, Настройки.Монитор.логПоказатьДату );
        ПолеЭлементы.setSelectedIndex( 3, Настройки.Монитор.логПоказатьВремя );
        ПолеЭлементы.setSelectedIndex( 4, Настройки.Монитор.логПоказатьПлан );
        ПолеЭлементы.setSelectedIndex( 5, Настройки.Монитор.логПоказатьРежим );
        ПолеЭлементы.setSelectedIndex( 6, Настройки.Монитор.логПоказатьДиаграмму );
        ПолеЭлементы.setSelectedIndex( 7, Настройки.Монитор.логПоказатьЛегенду );

        Форма.append( "[ Общие ]" );
        Форма.append( ПолеШиринаЭкрана );
        Форма.append( ПолеВысотаЭрана );
        Форма.append( ПолеОтступXY );
        Форма.append( ПолеШагСеткиXY );
        Форма.append( ПолеПолныйЭкран );

        // Разделитель.
        Форма.append( new Spacer ( 3, 10 ) );

        Форма.append( "[ Диаграмма ]" );
        Форма.append( ПолеДиаграммаОтступСверху );
        Форма.append( ПолеДиаграммаТолщинаПолосы );
        Форма.append( ПолеДиаграммаДлинаИнтервала );

        // Разделитель.
        Форма.append( new Spacer ( 3, 10 ) );
        Форма.append( "[ Время ]" );
        Форма.append( ПолеВремяОтступСверху );

        // Разделитель.
        Форма.append( new Spacer ( 3, 10 ) );
        Форма.append( "[ План ]" );
        Форма.append( ПолеПланОтступСверху );

        // Разделитель.
        Форма.append( new Spacer ( 3, 10 ) );
        Форма.append( "[ Режим ]" );
        Форма.append( ПолеРежимОтступСверху );

        // Разделитель.
        Форма.append( new Spacer ( 3, 10 ) );
        Форма.append( "[ Элементы ]" );
        Форма.append( ПолеЭлементы );

        Форма.addCommand( КомандаНазад );
        Форма.addCommand( КомандаПрименить );

        // Установка обработчика событий Формы.
        Форма.setCommandListener( this );
        Форма.setItemStateListener( this );

        main.Логгер.info( "[MonitorSettingsForm.java]: MonitorSettingsForm()" );
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

                main.Логгер.info( "[MonitorSettingsForm.java]: <Назад>" );
                Дисплей.setCurrent( РодительскоеОкно );


            } else if ( команда == КомандаПрименить ) {

                main.Логгер.info( "[MonitorSettingsForm.java]: <Применить>" );

                Настройки.Монитор.ШиринаЭкрана = Integer.parseInt( ПолеШиринаЭкрана.getString() );
                Настройки.Монитор.ВысотаЭкрана = Integer.parseInt( ПолеВысотаЭрана.getString() );
                Настройки.Монитор.ОтступXY = Integer.parseInt( ПолеОтступXY.getString() );
                Настройки.Монитор.ШагСеткиXY = Integer.parseInt( ПолеШагСеткиXY.getString() );
                Настройки.Монитор.ПолныйЭкран = ПолеПолныйЭкран.isSelected( 0 );

                Настройки.Монитор.ДиаграммаОтступСверху = Integer.parseInt( ПолеДиаграммаОтступСверху.getString() );
                Настройки.Монитор.ДиаграммаТолщинаПолосы = Integer.parseInt( ПолеДиаграммаТолщинаПолосы.getString() );
                Настройки.Монитор.ДиаграммаДлинаИнтервала = Integer.parseInt( ПолеДиаграммаДлинаИнтервала.getString() ); // в минутах;

                Настройки.Монитор.ВремяОтступСверху = Integer.parseInt( ПолеВремяОтступСверху.getString() );
                Настройки.Монитор.ПланОтступСверху = Integer.parseInt( ПолеПланОтступСверху.getString() );
                Настройки.Монитор.РежимОтступСверху = Integer.parseInt( ПолеРежимОтступСверху.getString() );

                Настройки.Монитор.логПоказатьСетку = ПолеЭлементы.isSelected( 0 );
                Настройки.Монитор.логПоказатьРамку = ПолеЭлементы.isSelected( 1 );
                Настройки.Монитор.логПоказатьДату = ПолеЭлементы.isSelected( 2 );
                Настройки.Монитор.логПоказатьВремя = ПолеЭлементы.isSelected( 3 );
                Настройки.Монитор.логПоказатьПлан = ПолеЭлементы.isSelected( 4 );
                Настройки.Монитор.логПоказатьРежим = ПолеЭлементы.isSelected( 5 );
                Настройки.Монитор.логПоказатьДиаграмму = ПолеЭлементы.isSelected( 6 );
                Настройки.Монитор.логПоказатьЛегенду = ПолеЭлементы.isSelected( 7 );

                Дисплей.setCurrent( new Alert( "Сообщение", "Настройки монитора сохранены.", null, AlertType.INFO ) );
            }

        }

    }


    public void itemStateChanged( Item элемент ) {

        if ( элемент == ПолеЭлементы ) {
        }

    }

    // </editor-fold>

}
