
// <editor-fold defaultstate="collapsed" desc=" Подключаемые модули ">

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

// </editor-fold>

public class SettingsForm implements CommandListener {

    // <editor-fold defaultstate="collapsed" desc=" Поля класса ">

    // Индексы элементов меню.
    static final public byte ЭЛЕМЕНТ_НАВЕРХ = 0;
    static final public byte ЭЛЕМЕНТ_ОСНОВНЫЕ = 1;
    static final public byte ЭЛЕМЕНТ_МОНИТОР = 2;
    static final public byte ЭЛЕМЕНТ_ЗВОНКИ = 3;
    static final public byte ЭЛЕМЕНТ_ПЛАНЫ = 4;
    static final public byte ЭЛЕМЕНТ_РАСПИСАНИЯ = 5;

    private Command КомандаНазад;
    private Command КомандаСохранить;
    private Command КомандаСохранитьКак;
    private Command КомандаЗагрузить;
    private Command КомандаУдалить;
    private Command КомандаИмпорт;
    private Command КомандаЭкспорт;

    private List МенюНастройки;

    private MIDlet Мидлет;
    private Display Дисплей;
    private Displayable РодительскоеОкно;
    private Schedules МенеджерРасписаний;
    private Settings Настройки;

    // </editor-fold>

    // <editor-fold desc=" Конструктор ">

    public SettingsForm( MIDlet Мидлет, Displayable Окно, Schedules Менеджер, Settings Настройки ) {

        this.Мидлет = Мидлет;
        Дисплей = Display.getDisplay( Мидлет );
        РодительскоеОкно = Окно;
        МенеджерРасписаний = Менеджер;
        this.Настройки = Настройки;

        // Создаём команды.
        КомандаНазад = new Command( "Назад", Command.BACK, 0 );
        КомандаСохранить = new Command( "Сохранить", Command.SCREEN, 1 );
        КомандаСохранитьКак = new Command( "Сохранить как...", Command.SCREEN, 2 );
        КомандаЗагрузить = new Command( "Загрузить...", Command.SCREEN, 3 );
        КомандаУдалить = new Command( "Удалить...", Command.SCREEN, 4 );
        КомандаИмпорт = new Command( "Импорт...", Command.SCREEN, 5 );
        КомандаЭкспорт = new Command( "Экспорт...", Command.SCREEN, 6 );

        // Экран Списка настроек.
        МенюНастройки = new List( "Настройки", List.IMPLICIT );
        String[] ЭлементыМеню = {
            "..",
            "Основные",
            "Монитор",
            "Звонки",
            "Планы",
            "Расписания"
        };

        for ( int ii = 0; ii < ЭлементыМеню.length; ii++ ){

            МенюНастройки.append( ЭлементыМеню[ii], null );
        }

        МенюНастройки.addCommand( КомандаНазад );
        МенюНастройки.addCommand( КомандаСохранить );
        МенюНастройки.addCommand( КомандаСохранитьКак );
        МенюНастройки.addCommand( КомандаЗагрузить );
        МенюНастройки.addCommand( КомандаУдалить );
        МенюНастройки.addCommand( КомандаИмпорт );
        МенюНастройки.addCommand( КомандаЭкспорт );

        // Установка обработчика событий Формы.
        МенюНастройки.setCommandListener( this );

        main.Логгер.info( "[SettingsForm.java]: SettingsForm()" );
    }

    // </editor-fold>

    // <editor-fold desc=" Методы класса ">

    public void Отобразить() {

        Дисплей.setCurrent( МенюНастройки );
    }

    // </editor-fold>

    // <editor-fold desc=" Обработчики событий ">

    public void commandAction( Command команда, Displayable элемент ) {

        if ( элемент == МенюНастройки ) {

            // Обработка команд.
            if ( команда == List.SELECT_COMMAND ) {

                main.Логгер.info( "[SettingsForm.java]: -> \\Меню\\Настройки\\" + МенюНастройки.getString( МенюНастройки.getSelectedIndex() ) + "\\" );

                switch ( МенюНастройки.getSelectedIndex() ) {

                    case ЭЛЕМЕНТ_НАВЕРХ:

                        Дисплей.setCurrent( РодительскоеОкно );
                        break;

                    case ЭЛЕМЕНТ_ОСНОВНЫЕ:

                        ( new GeneralSettingsForm( Мидлет, МенюНастройки, Настройки ) ).Отобразить();
                        break;

                    case ЭЛЕМЕНТ_МОНИТОР:

                        ( new MonitorSettingsForm( Мидлет, МенюНастройки, Настройки ) ).Отобразить();
                        break;

                    case ЭЛЕМЕНТ_ЗВОНКИ:

                        ( new BellsSettingsForm( Мидлет, МенюНастройки, МенеджерРасписаний, Настройки ) ).Отобразить();
                        break;

                    case ЭЛЕМЕНТ_ПЛАНЫ:

                        ( new PlansSettingsForm( Мидлет, МенюНастройки, МенеджерРасписаний, Настройки ) ).Отобразить();
                        break;

                    case ЭЛЕМЕНТ_РАСПИСАНИЯ:

                        ( new SchedulesSettingsForm( Мидлет, МенюНастройки, МенеджерРасписаний, Настройки ) ).Отобразить();
                        break;
                }


            // Переход назад.
            } else if ( команда == КомандаНазад ) {

                main.Логгер.info( "[SettingsForm.java]: <Назад>" );
                Дисплей.setCurrent( РодительскоеОкно );


            } else if ( команда == КомандаСохранить ) {

                main.Логгер.info( "[SettingsForm.java]: <Сохранить>" );
                Настройки.СохранитьНастройки( Настройки.Основные.ИмяВХранилище );
                String Текст = "Настройки сохранены в хранилище.";
                Дисплей.setCurrent( new Alert( "Сообщение", Текст, null , AlertType.INFO ) );


            } else if ( команда == КомандаСохранитьКак ) {

                main.Логгер.info( "[SettingsForm.java]: <СохранитьКак>" );
                ( new SettingsSaveAsForm( Мидлет, МенюНастройки, Настройки ) ).Отобразить();


            } else if ( команда == КомандаЗагрузить ) {

                main.Логгер.info( "[SettingsForm.java]: <Загрузить>" );
                ( new SettingsLoadForm( Мидлет, МенюНастройки, Настройки ) ).Отобразить();


            } else if ( команда == КомандаУдалить ) {

                main.Логгер.info( "[SettingsForm.java]: <Удалить>)" );
                ( new SettingDeleteForm( Мидлет, МенюНастройки, Настройки ) ).Отобразить();


            } else if ( команда == КомандаИмпорт ) {

                main.Логгер.info( "[SettingsForm.java]: <Импорт>" );
                ( new SettingsImportForm( Мидлет, МенюНастройки, Настройки ) ).Отобразить();


            } else if ( команда == КомандаЭкспорт ) {

                main.Логгер.info( "[SettingsForm.java]: <Экспорт>" );
                ( new SettingsExportForm( Мидлет, МенюНастройки, Настройки ) ).Отобразить();
            }

        }

    }

    // </editor-fold>

}


