
// <editor-fold defaultstate="collapsed" desc=" Подключаемые модули ">

import java.util.*;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

// </editor-fold>

public class PlannerForm extends Canvas implements CommandListener {

    // <editor-fold defaultstate="collapsed" desc=" Поля класса ">

    static final public byte КНОПКА_ЗВЁЗДОЧКА = Canvas.KEY_STAR;

    private Command КомандаНазад = new Command( "Назад", Command.BACK, 0 );

    MIDlet Мидлет = null;
    private Display Дисплей;
    private Displayable РодительскоеОкно;
    private Settings Настройки;
    private Schedules МенеджерРасписаний;
    Planner Планировщик = null;

    // </editor-fold>

    // <editor-fold desc=" Конструктор ">

    public PlannerForm( MIDlet Мидлет, Displayable Окно, Schedules Менеджер, Settings Настройки, int НомерПлана ) {

        super();

        this.Мидлет = Мидлет;
        this.Дисплей = Display.getDisplay( Мидлет );
        this.РодительскоеОкно = Окно;
        this.Настройки = Настройки;
        this.МенеджерРасписаний = Менеджер;

        // Добавляем команды.
        this.addCommand( КомандаНазад );

        Планировщик = new Planner( Мидлет, this, Менеджер, Настройки, НомерПлана, new Date() );

        // Установка обработчика событий Формы.
        this.setCommandListener( this );

        Schedules.ОбразПлана План;
        План = ( Schedules.ОбразПлана ) МенеджерРасписаний.СписокПланов.Элементы.elementAt( НомерПлана );

        main.Логгер.info( "[PlannerForm.java]: PlannerForm( \"" + План.Название + "\" )" );
    }

    // </editor-fold>

    // <editor-fold desc=" Методы класса ">

    public void Отобразить() {

        this.setFullScreenMode( Настройки.Монитор.ПолныйЭкран );
        Дисплей.setCurrent( this );
    }


    public String КодВСимвол( int КодКнопки ) {

        switch ( КодКнопки ) {

            case Canvas.DOWN: return "ВНИЗ";

            case Canvas.FIRE: return "ВВОД";

            case Canvas.GAME_A: return "A";

            case Canvas.GAME_B: return "B";

            case Canvas.GAME_C: return "C";

            case Canvas.GAME_D: return "D";

            case Canvas.LEFT: return "ВЛЕВО";

            case Canvas.RIGHT: return "ВПРАВО";

            case Canvas.UP: return "ВВЕРХ";

        }

        return "-";
    }


    public void ПоказатьРамку( Graphics Холст, int ШиринаЭкрана, int ВысотаЭрана, int ОтступXY, int ШагСеткиXY  ){

        int СтарыйЦвет = Холст.getColor();

        Холст.setColor( 10, 80, 200 );
        Холст.drawRoundRect( ОтступXY, ОтступXY,
            ШиринаЭкрана - ОтступXY - 2, ВысотаЭрана - ОтступXY - 2, ШагСеткиXY, ШагСеткиXY );

        Холст.setColor( СтарыйЦвет );

    }


    public void ПоказатьСетку( Graphics Холст, int ШиринаЭкрана, int ВысотаЭрана, int ОтступXY, int ШагСеткиXY ) {

        int nh = ВысотаЭрана / ШагСеткиXY;
        int nw = ШиринаЭкрана / ШагСеткиXY;
        int СтарыйЦвет = Холст.getColor();

        Холст.setColor( 218, 218, 218 );
        Холст.setStrokeStyle( Graphics.DOTTED );

        for ( int i = 0; i <= nh; i++ ) {

            Холст.drawLine( ОтступXY, i*ШагСеткиXY, ШиринаЭкрана - ОтступXY - 2, i*ШагСеткиXY );
        }

        for ( int i = 0; i <= nw; i++ ) {

            Холст.drawLine( i*ШагСеткиXY, ОтступXY, i*ШагСеткиXY, ВысотаЭрана - ОтступXY - 2 );
        }

        Холст.setStrokeStyle( Graphics.SOLID );
        Холст.setColor( СтарыйЦвет );

    }


    public void ОчиститьХолст( Graphics Холст ){

        Холст.setColor( 255, 255, 255 );
        //Холст.fillRect( 0, 0, Настройки.Монитор.ШиринаЭкрана, Настройки.Монитор.ВысотаЭрана );
        Холст.fillRect( 0, 0, getWidth(), getHeight() );

    }

    // </editor-fold>

    // <editor-fold desc=" Обработчики событий ">

    protected void keyPressed( int КодКнопки ) {

        main.Логгер.info( "[PlannerForm.java]: Клавиша " + "["
                + ( ( КодКнопки > 0 ) ? "'" + (char) КодКнопки + "'" : "" + КодКнопки )
                + "]: "
                + КодВСимвол( getGameAction( КодКнопки ) ) );

        if ( КодКнопки == КНОПКА_ЗВЁЗДОЧКА ) {

            Планировщик.СменитьФокус();
            repaint();

        } else {

            Планировщик.keyPressed( getGameAction( КодКнопки ) );
            repaint();
        }

    }


    protected void paint( Graphics Холст ) {

        ОчиститьХолст( Холст );

        if ( Настройки.Монитор.логПоказатьСетку )
            ПоказатьСетку( Холст,
                Настройки.Монитор.ШиринаЭкрана,
                Настройки.Монитор.ВысотаЭкрана,
                Настройки.Монитор.ОтступXY,
                Настройки.Монитор.ШагСеткиXY
                );

//        if ( Настройки.Монитор.логПоказатьРамку )
//            ПоказатьРамку( Холст,
//                Настройки.Монитор.ШиринаЭкрана,
//                Настройки.Монитор.ВысотаЭрана,
//                Настройки.Монитор.ОтступXY,
//                Настройки.Монитор.ШагСеткиXY
//                );

        Планировщик.paint( Холст );

    }


    public void commandAction( Command команда, Displayable элемент ) {

        // Переход в Главное меню.
        if ( команда == КомандаНазад ) {

            main.Логгер.info( "[PlannerForm.java]: <Назад>" );
            Дисплей.setCurrent( РодительскоеОкно );
        }

    }

    // </editor-fold>
    
}
