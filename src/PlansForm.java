
// <editor-fold defaultstate="collapsed" desc=" Подключаемые модули ">

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

// </editor-fold>

public class PlansForm implements CommandListener {

    // <editor-fold defaultstate="collapsed" desc=" Поля класса ">

    // Индексы элементов меню.
    static final public byte ЭЛЕМЕНТ_НАВЕРХ = 0;

    private Command КомандаНазад = new Command( "Назад", Command.BACK, 0 );
    private Command КомандаКлонировать = new Command( "Клонировать", Command.SCREEN, 1 );
    private Command КомандаИзменить = new Command( "Изменить...", Command.SCREEN, 2 );
    private Command КомандаУдалить = new Command( "Удалить", Command.SCREEN, 3 );
    private Command КомандаИмпорт = new Command( "Импорт...", Command.SCREEN, 4 );
    private Command КомандаЭкспорт = new Command( "Экспорт...", Command.SCREEN, 5 );

    public List Список;

    private MIDlet Мидлет;
    private Display Дисплей;
    private Displayable РодительскоеОкно;
    private Schedules МенеджерРасписаний;
    private Settings Настройки;

    // </editor-fold>

    // <editor-fold desc=" Конструктор ">

    public PlansForm( MIDlet Мидлет, Displayable Окно, Schedules Менеджер, Settings Настройки ) {

        this.Мидлет = Мидлет;
        Дисплей = Display.getDisplay( Мидлет );
        РодительскоеОкно = Окно;
        МенеджерРасписаний = Менеджер;
        this.Настройки = Настройки;

        Список = new List( "Планы", List.IMPLICIT );

        Список.addCommand( КомандаНазад );
        Список.addCommand( КомандаИзменить );
        Список.addCommand( КомандаКлонировать );
        Список.addCommand( КомандаУдалить );
//        МенюНастройки.addCommand( КомандаИмпорт );
//        МенюНастройки.addCommand( КомандаЭкспорт );

        Обновить();

        // Установка обработчика событий Формы.
        Список.setCommandListener( this );

        main.Логгер.info( "[PlansForm.java]: PlansForm()" );
    }

    // </editor-fold>

    // <editor-fold desc=" Методы класса ">

    public void Отобразить() {

        Дисплей.setCurrent( Список );
    }


    public void Обновить() {

        Schedules.ОбразПлана План;

        Список.deleteAll();

        Список.append( "..", null );

        for ( int ii = 0; ii < МенеджерРасписаний.СписокПланов.Элементы.size(); ii++ ) {

            План = ( Schedules.ОбразПлана ) МенеджерРасписаний.СписокПланов.Элементы.elementAt(ii);
            Список.append( План.Название , null);
        }

    }

    // </editor-fold>

    // <editor-fold desc=" Обработчики событий ">

    public void commandAction( Command команда, Displayable элемент ) {

        if ( элемент == Список ) {

            // Обработка команд.
            if ( команда == List.SELECT_COMMAND ) {

                if ( Список.getSelectedIndex() == ЭЛЕМЕНТ_НАВЕРХ ) {

                    main.Логгер.info( "[PlansForm.java]: -> \\Меню\\" );
                    Дисплей.setCurrent( РодительскоеОкно );

                } else {

                    //Дисплей.setCurrent( new Alert( "Сообщение", "Для редактирования плана используйте Планировщик.", null, AlertType.INFO ) );
                    ( new PlannerForm( Мидлет, Список, МенеджерРасписаний, Настройки, Список.getSelectedIndex() - 1 ) ).Отобразить();
                }


            } else if ( команда == КомандаНазад ) {

                main.Логгер.info( "[PlansForm.java]: <Назад>" );
                Дисплей.setCurrent( РодительскоеОкно );


            } else if ( команда == КомандаКлонировать ) {

                main.Логгер.info( "[PlansForm.java]: <Клонировать>" );
                if ( Список.getSelectedIndex() == ЭЛЕМЕНТ_НАВЕРХ ) return;
                МенеджерРасписаний.КлонироватьПлан( Список.getSelectedIndex() - 1 );
                Обновить();


            } else if ( команда == КомандаИзменить ) {

                main.Логгер.info( "[PlansForm.java]: <Изменить>" );
                if ( Список.getSelectedIndex() == ЭЛЕМЕНТ_НАВЕРХ ) return;
                ( new EditPlanNameForm( Мидлет, this, МенеджерРасписаний, Список.getSelectedIndex() - 1 ) ).Отобразить();


            } else if ( команда == КомандаУдалить ) {

                main.Логгер.info( "[PlansForm.java]: <Удалить>" );
                if ( Список.getSelectedIndex() == ЭЛЕМЕНТ_НАВЕРХ ) return;
                МенеджерРасписаний.УдалитьПлан( Список.getSelectedIndex() - 1 );
                Обновить();

            }

        }

    }

    // </editor-fold>

}


