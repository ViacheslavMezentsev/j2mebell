
// <editor-fold defaultstate="collapsed" desc=" Подключаемые модули ">

import java.util.*;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

// </editor-fold>

public class Planner {

    // <editor-fold defaultstate="collapsed" desc=" Поля класса ">

    static final public byte ФОКУС_ПЛАН = 0;
    static final public byte ФОКУС_ТИП = 1;
    static final public byte ФОКУС_ГОД = 2;
    static final public byte ФОКУС_МЕСЯЦ = 3;
    static final public byte ФОКУС_ЦВЕТ = 4;
    static final public byte ФОКУС_ВЫДЕЛИТЬ_ВСЁ = 5;
    static final public byte ФОКУС_ДНИ = 6;
    static final public byte ФОКУС_НЕДЕЛИ = 7;
    static final public byte ФОКУС_ЧИСЛА = 8;

    static final public byte КНОПКА_ВВОД = Canvas.FIRE;
    static final public byte КНОПКА_ВНИЗ = Canvas.DOWN;
    static final public byte КНОПКА_ВВЕРХ = Canvas.UP;
    static final public byte КНОПКА_ВПРАВО = Canvas.RIGHT;
    static final public byte КНОПКА_ВЛЕВО = Canvas.LEFT;

    static final public int ЦВЕТ_КРАСНЫЙ = 0xFF0000;
    static final public int ЦВЕТ_СИНИЙ = 0x00FF00;
    static final public int ЦВЕТ_ЗЕЛЁНЫЙ = 0x0000FF;

    static final public byte ПЛАН_ОСНОВНОЙ = 0;
    static final public byte ПЛАН_ДОПОЛНИТЕЛЬНЫЙ = 1;

    static final String[] MONTH_LABELS = new String[]{
        "Янв", "Фев", "Март", "Апр", "Май", "Июнь",
        "Июль", "Авг", "Сен", "Окт", "Ноя", "Дек"
    };
    static final String[] WEEKDAY_LABELS = new String[] {
        "П", "В", "С", "Ч", "П", "С", "В"
    };

    private int ВыбранныйЭлемент;
    private int ДеньНедели;
    private int НомерНедели;
    private int ТипПлана;
    private int НомерПлана;
    public int НомерРасписания;
    private int КолвоРасписаний;

    private int ВысотаЯчейки;
    private int ШиринаЯчейки;

    private Vector НомераЗаписейРасписаний;

    /* starting week day: 0 for monday, 6 for sunday */
    public int startWeekday = 0;

    /* internal properties */
    int width = 0;
    int height = 0;
    int headerHeight = 0;
    int weekHeight = 0;
    int cellWidth = 0;
    int cellHeight = 0;

    /* internal time properties */
    long currentTimestamp = 0;
    Calendar calendar = null;
    int weeks = 0;

    MIDlet Мидлет = null;
    private Display Дисплей;
    private Displayable РодительскоеОкно;
    private Settings Настройки;
    private Schedules МенеджерРасписаний;

    ОбразОбластиЗаголовока ОбластьЗаголовка;
    ОбразОбластиНазвания ОбластьНазвания;
    ОбразОбластиТипа ОбластьТипа;
    ОбразОбластиГода ОбластьГода;
    ОбразОбластиМесяца ОбластьМесяца;
    ОбразОбластиЦвета ОбластьЦвета;
    ОбразОбластиВсё ОбластьВсё;
    ОбразОбластиНедель ОбластьНедель;
    ОбразОбластиДней ОбластьДней;
    ОбразОбластиЧисел ОбластьЧисел;
    ОбразОбластиСтатуса ОбластьСтатуса;

    // </editor-fold>

    class ОбразОбластиЗаголовока {
        private int ОтступСверху;
        private int ОтступСлева;
        private int Ширина;
        private int Высота;

        private String Текст;

        ОбразОбластиЗаголовока( int ОтступСверху, int ОтступСлева, String Текст ) {
            this.ОтступСверху = ОтступСверху;
            this.ОтступСлева = ОтступСлева;
            this.Текст = Текст;
            Ширина = ШиринаЯчейки * 8 + ШиринаЯчейки / 2 - 4;
            Высота = ВысотаЯчейки * 1 - 4;
        }

        public void Нарисовать( Graphics Холст ) {
            Font Шрифт = Font.getDefaultFont();
            int ШиринаТекста = Шрифт.stringWidth( Текст );
            int ВысотаТекста = Шрифт.getHeight();

            Холст.drawString( Текст, ОтступСлева + ( Ширина - ШиринаТекста ) / 2 ,
                    ОтступСверху + ( Высота - ВысотаТекста ) / 2,
                    Graphics.TOP | Graphics.LEFT );
        }
    }

    class ОбразОбластиНазвания {
        private int ОтступСверху;
        private int ОтступСлева;
        private int Ширина;
        private int Высота;

        ОбразОбластиНазвания( int ОтступСверху, int ОтступСлева ) {
            this.ОтступСверху = ОтступСверху;
            this.ОтступСлева = ОтступСлева;
            Ширина = ШиринаЯчейки * 8 + ШиринаЯчейки / 2 - 4;
            Высота = ВысотаЯчейки * 1 - 4;
        }

        public void Нарисовать( Graphics Холст, boolean Выделен ) {
            Font Шрифт = Font.getDefaultFont();
            Schedules.ОбразПлана План;

            String Текст;
            План = ( Schedules.ОбразПлана ) МенеджерРасписаний.СписокПланов.Элементы.elementAt( НомерПлана );
            Текст = "<" + План.Название + ">";

            int ШиринаТекста = Шрифт.stringWidth( Текст );
            int ВысотаТекста = Шрифт.getHeight();

            if ( Выделен ) {
                int СтарыйЦвет = Холст.getColor();
                Холст.setColor( 10, 80, 200 );
                Холст.setStrokeStyle( Graphics.SOLID );
                Холст.drawRect( ОтступСлева, ОтступСверху, Ширина, Высота );
                Холст.setColor( СтарыйЦвет );
            } else {
                Холст.setStrokeStyle( Graphics.DOTTED );
                Холст.drawRect( ОтступСлева, ОтступСверху, Ширина, Высота );
                Холст.setStrokeStyle( Graphics.SOLID );
            }

            Холст.drawString( Текст, ОтступСлева + ( Ширина - ШиринаТекста ) / 2 ,
                    ОтступСверху + ( Высота - ВысотаТекста ) / 2,
                    Graphics.TOP | Graphics.LEFT );
        }

        public void ОбработкаКлавиш( int Кнопка ) {
            switch( Кнопка ) {
                case КНОПКА_ВВОД:
                    break;
                case КНОПКА_ВВЕРХ:
                    break;
                case КНОПКА_ВНИЗ:
                    break;
                case КНОПКА_ВПРАВО:
                    НомерПлана += 1;
                    НомерПлана %= МенеджерРасписаний.СписокПланов.Элементы.size();
                    break;
                case КНОПКА_ВЛЕВО:
                    if ( НомерПлана == 0 ) НомерПлана = МенеджерРасписаний.СписокПланов.Элементы.size();
                    НомерПлана -= 1;
                    break;
            }
        }
    }

    class ОбразОбластиТипа {
        private int ОтступСверху;
        private int ОтступСлева;
        private int Ширина;
        private int Высота;

        ОбразОбластиТипа( int ОтступСверху, int ОтступСлева ) {
            this.ОтступСверху = ОтступСверху;
            this.ОтступСлева = ОтступСлева;
            Ширина = ШиринаЯчейки * 2 - 4;
            Высота = ВысотаЯчейки * 1 - 4;
        }

        public void Нарисовать( Graphics Холст, boolean Выделен ) {
            Font Шрифт = Font.getDefaultFont();
            String Текст;

            switch ( ТипПлана ) {
                case ПЛАН_ОСНОВНОЙ:
                    Текст = "<Осн>";
                    break;
                case ПЛАН_ДОПОЛНИТЕЛЬНЫЙ:
                    Текст = "<Доп>";
                    break;
                default:
                    Текст = "<нет>";
            }

            int ШиринаТекста = Шрифт.stringWidth( Текст );
            int ВысотаТекста = Шрифт.getHeight();

            if ( Выделен ) {
                int СтарыйЦвет = Холст.getColor();
                Холст.setColor( 10, 80, 200 );
                Холст.setStrokeStyle( Graphics.SOLID );
                Холст.drawRect( ОтступСлева, ОтступСверху, Ширина, Высота );
                Холст.setColor( СтарыйЦвет );
            } else {
                Холст.setStrokeStyle( Graphics.DOTTED );
                Холст.drawRect( ОтступСлева, ОтступСверху, Ширина, Высота );
                Холст.setStrokeStyle( Graphics.SOLID );
            }

            Холст.drawString( Текст, ОтступСлева + ( Ширина - ШиринаТекста ) / 2 ,
                    ОтступСверху + ( Высота - ВысотаТекста ) / 2,
                    Graphics.TOP | Graphics.LEFT );
        }

        public void ОбработкаКлавиш( int Кнопка ) {
            switch( Кнопка ) {
                case КНОПКА_ВВОД:
                    break;
                case КНОПКА_ВВЕРХ:
                    break;
                case КНОПКА_ВНИЗ:
                    break;
                case КНОПКА_ВПРАВО:
                case КНОПКА_ВЛЕВО:
                    switch ( ТипПлана ) {
                        case ПЛАН_ОСНОВНОЙ:
                            ТипПлана = ПЛАН_ДОПОЛНИТЕЛЬНЫЙ;
                            break;
                        case ПЛАН_ДОПОЛНИТЕЛЬНЫЙ:
                            ТипПлана = ПЛАН_ОСНОВНОЙ;
                            break;
                    }
                    break;
            }
        }
    }

    class ОбразОбластиГода {
        private int ОтступСверху;
        private int ОтступСлева;
        private int Ширина;
        private int Высота;

        ОбразОбластиГода( int ОтступСверху, int ОтступСлева ) {
            this.ОтступСверху = ОтступСверху;
            this.ОтступСлева = ОтступСлева;
            Ширина = ШиринаЯчейки * 3 + ШиринаЯчейки / 2 - 4;
            Высота = ВысотаЯчейки * 1 - 4;
        }

        public void Нарисовать( Graphics Холст, boolean Выделен ) {
            Font Шрифт = Font.getDefaultFont();
            String Текст = "<" + Integer.toString( calendar.get( Calendar.YEAR ) ) + ">";
            int ШиринаТекста = Шрифт.stringWidth( Текст );
            int ВысотаТекста = Шрифт.getHeight();

            if ( Выделен ) {
                int СтарыйЦвет = Холст.getColor();
                Холст.setColor( 10, 80, 200 );
                Холст.setStrokeStyle( Graphics.SOLID );
                Холст.drawRect( ОтступСлева, ОтступСверху, Ширина, Высота );
                Холст.setColor( СтарыйЦвет );
            } else {
                Холст.setStrokeStyle( Graphics.DOTTED );
                Холст.drawRect( ОтступСлева, ОтступСверху, Ширина, Высота );
                Холст.setStrokeStyle( Graphics.SOLID );
            }

            Холст.drawString( Текст, ОтступСлева + ( Ширина - ШиринаТекста ) / 2 ,
                    ОтступСверху + ( Высота - ВысотаТекста ) / 2,
                    Graphics.TOP | Graphics.LEFT );
        }

        public void ОбработкаКлавиш( int Кнопка ) {
            int Год;

            switch( Кнопка ) {
                case КНОПКА_ВВОД:
                    break;
                case КНОПКА_ВВЕРХ:
                    break;
                case КНОПКА_ВНИЗ:
                    break;
                case КНОПКА_ВПРАВО:
                    Год = calendar.get( Calendar.YEAR );
                    Год += 1;
                    calendar.set( Calendar.YEAR, Год );
                    currentTimestamp = getSelectedDate().getTime();
                    weeks = ( int ) Math.ceil( ( ( double ) getStartWeekday() + getMonthDays() ) / 7 );
                    break;
                case КНОПКА_ВЛЕВО:
                    Год = calendar.get( Calendar.YEAR );
                    Год -= 1;
                    calendar.set( Calendar.YEAR, Год );
                    currentTimestamp = getSelectedDate().getTime();
                    weeks = ( int ) Math.ceil( ( ( double ) getStartWeekday() + getMonthDays() ) / 7 );
                    break;
            }
        }
    }

    class ОбразОбластиМесяца {
        private int ОтступСверху;
        private int ОтступСлева;
        private int Ширина;
        private int Высота;

        ОбразОбластиМесяца( int ОтступСверху, int ОтступСлева ) {
            this.ОтступСверху = ОтступСверху;
            this.ОтступСлева = ОтступСлева;
            Ширина = ШиринаЯчейки * 3 - 4;
            Высота = ВысотаЯчейки * 1 - 4;
        }

        public void Нарисовать( Graphics Холст, boolean Выделен ) {
            Font Шрифт = Font.getDefaultFont();

            String Текст = "<" + MONTH_LABELS[ calendar.get( Calendar.MONTH ) ] + ">";
            int ШиринаТекста = Шрифт.stringWidth( Текст );
            int ВысотаТекста = Шрифт.getHeight();

            if ( Выделен ) {
                int СтарыйЦвет = Холст.getColor();
                Холст.setColor( 10, 80, 200 );
                Холст.setStrokeStyle( Graphics.SOLID );
                Холст.drawRect( ОтступСлева, ОтступСверху, Ширина, Высота );
                Холст.setColor( СтарыйЦвет );
            } else {
                Холст.setStrokeStyle( Graphics.DOTTED );
                Холст.drawRect( ОтступСлева, ОтступСверху, Ширина, Высота );
                Холст.setStrokeStyle( Graphics.SOLID );
            }

            Холст.drawString( Текст, ОтступСлева + ( Ширина - ШиринаТекста ) / 2 ,
                    ОтступСверху + ( Высота - ВысотаТекста ) / 2,
                    Graphics.TOP | Graphics.LEFT );
        }

        public void ОбработкаКлавиш( int Кнопка ) {
            int Месяц;

            switch( Кнопка ) {
                case КНОПКА_ВВОД:
                    break;
                case КНОПКА_ВВЕРХ:
                    break;
                case КНОПКА_ВНИЗ:
                    break;
                case КНОПКА_ВПРАВО:
                    Месяц = calendar.get( Calendar.MONTH );
                    if ( Месяц == 11 ) {
                        int Год = calendar.get( Calendar.YEAR );
                        Год += 1;
                        calendar.set( Calendar.YEAR, Год );
                    }
                    Месяц += 1;
                    Месяц %= 12;
                    calendar.set( Calendar.DAY_OF_MONTH, 1 );
                    calendar.set( Calendar.MONTH, Месяц );
                    currentTimestamp = getSelectedDate().getTime();
                    weeks = ( int ) Math.ceil( ( ( double ) getStartWeekday() + getMonthDays() ) / 7 );
                    break;
                case КНОПКА_ВЛЕВО:
                    Месяц = calendar.get( Calendar.MONTH );
                    if ( Месяц == 0 ) {
                        int Год = calendar.get( Calendar.YEAR );
                        Год -= 1;
                        calendar.set( Calendar.YEAR, Год );
                        Месяц = 12;
                    }
                    Месяц -= 1;
                    calendar.set( Calendar.DAY_OF_MONTH, 1 );
                    calendar.set( Calendar.MONTH, Месяц );
                    currentTimestamp = getSelectedDate().getTime();
                    weeks = ( int ) Math.ceil( ( ( double ) getStartWeekday() + getMonthDays() ) / 7 );
                    break;
            }
        }
    }

    class ОбразОбластиЦвета {
        private int ОтступСверху;
        private int ОтступСлева;
        private int Ширина;
        private int Высота;

        ОбразОбластиЦвета( int ОтступСверху, int ОтступСлева ) {
            this.ОтступСверху = ОтступСверху;
            this.ОтступСлева = ОтступСлева;
            Ширина = ШиринаЯчейки * 1 - 4;
            Высота = ВысотаЯчейки * 1 - 4;
        }

        public void Нарисовать( Graphics Холст, boolean Выделен ) {
            Font Шрифт = Font.getDefaultFont();
            int СтарыйЦвет;

            String Текст = "<>";
            int ШиринаТекста = Шрифт.stringWidth( Текст );
            int ВысотаТекста = Шрифт.getHeight();

            if ( Выделен ) {
                СтарыйЦвет = Холст.getColor();
                Холст.setColor( 10, 80, 200 );
                Холст.setStrokeStyle( Graphics.SOLID );
                Холст.drawRect( ОтступСлева, ОтступСверху, Ширина, Высота );
                Холст.setColor( СтарыйЦвет );
            } else {
                Холст.setStrokeStyle( Graphics.DOTTED );
                Холст.drawRect( ОтступСлева, ОтступСверху, Ширина, Высота );
                Холст.setStrokeStyle( Graphics.SOLID );
            }

            СтарыйЦвет = Холст.getColor();
            int N = ( ( Integer ) НомераЗаписейРасписаний.elementAt( НомерРасписания ) ).intValue();
            Холст.setColor( ЦветРасписанияN(N) );

            Холст.fillRect( ОтступСлева + 1, ОтступСверху + 1,
                    Ширина - 1, Высота - 1 );
            Холст.setColor( СтарыйЦвет );

            Холст.drawString( Текст, ОтступСлева + ( Ширина - ШиринаТекста ) / 2 ,
                    ОтступСверху + ( Высота - ВысотаТекста ) / 2,
                    Graphics.TOP | Graphics.LEFT );
        }

        public void ОбработкаКлавиш( int Кнопка ) {
            switch( Кнопка ) {
                case КНОПКА_ВВОД:
                    break;

                case КНОПКА_ВВЕРХ:
                    break;

                case КНОПКА_ВНИЗ:
                    break;

                case КНОПКА_ВПРАВО:
                    НомерРасписания += 1;
                    НомерРасписания %= КолвоРасписаний;
                    break;

                case КНОПКА_ВЛЕВО:
                    if ( НомерРасписания == 0 ) НомерРасписания = КолвоРасписаний;
                    НомерРасписания -= 1;
                    break;
            }
        }
    }

    class ОбразОбластиВсё {
        private int ОтступСверху;
        private int ОтступСлева;
        private int Ширина;
        private int Высота;

        ОбразОбластиВсё( int ОтступСверху, int ОтступСлева ) {
            this.ОтступСверху = ОтступСверху;
            this.ОтступСлева = ОтступСлева;
            Ширина = ШиринаЯчейки * 1 - 4;
            Высота = ВысотаЯчейки * 1 - 4;
        }

        public void Нарисовать( Graphics Холст, boolean Выделен ) {
            Font Шрифт = Font.getDefaultFont();
            int СтарыйЦвет;

            String Текст = "+";
            int ШиринаТекста = Шрифт.stringWidth( Текст );
            int ВысотаТекста = Шрифт.getHeight();

            if ( Выделен ) {
                СтарыйЦвет = Холст.getColor();
                Холст.setColor( 10, 80, 200 );
                Холст.setStrokeStyle( Graphics.SOLID );
                Холст.drawRect( ОтступСлева, ОтступСверху, Ширина, Высота );
                Холст.setColor( СтарыйЦвет );
            } else {
                Холст.setStrokeStyle( Graphics.DOTTED );
                Холст.drawRect( ОтступСлева, ОтступСверху, Ширина, Высота );
                Холст.setStrokeStyle( Graphics.SOLID );
            }

            СтарыйЦвет = Холст.getColor();

            if ( Выделен  ) {
                int N = ( ( Integer ) НомераЗаписейРасписаний.elementAt( НомерРасписания ) ).intValue();
                Холст.setColor( ЦветРасписанияN(N) );

                Холст.fillRect( ОтступСлева + 1, ОтступСверху + 1,
                        Ширина - 1, Высота - 1 );
                Холст.setColor( СтарыйЦвет );
            }

            Холст.drawString( Текст, ОтступСлева + ( Ширина - ШиринаТекста ) / 2 ,
                    ОтступСверху + ( Высота - ВысотаТекста ) / 2,
                    Graphics.TOP | Graphics.LEFT );
        }

        public void ОбработкаКлавиш( int Кнопка ) {
            int Месяц;

            switch( Кнопка ) {
                case КНОПКА_ВВОД:
                    int месяц = calendar.get( Calendar.MONTH );
                    int N;

                    Schedules.ОбразПлана План;
                    План = ( Schedules.ОбразПлана ) МенеджерРасписаний.СписокПланов.Элементы.elementAt( НомерПлана );
                    N = ( ( Integer ) НомераЗаписейРасписаний.elementAt( НомерРасписания ) ).intValue();

                    for ( int ii = 0; ii < 31; ii++ ) {
                        switch ( ТипПлана ) {
                            case ПЛАН_ОСНОВНОЙ:
                                План.Основной[ месяц ][ii] = new Integer(N);
                                break;
                            case ПЛАН_ДОПОЛНИТЕЛЬНЫЙ:
                                План.Дополнительный[ месяц ][ii] = new Integer(N);
                                break;
                        }
                    }
                    break;

                case КНОПКА_ВВЕРХ:
                    Месяц = calendar.get( Calendar.MONTH );
                    if ( Месяц == 0 ) {
                        int Год = calendar.get( Calendar.YEAR );
                        Год -= 1;
                        calendar.set( Calendar.YEAR, Год );
                        Месяц = 12;
                    }
                    Месяц -= 1;
                    calendar.set( Calendar.DAY_OF_MONTH, 1 );
                    calendar.set( Calendar.MONTH, Месяц );
                    currentTimestamp = getSelectedDate().getTime();
                    weeks = ( int ) Math.ceil( ( ( double ) getStartWeekday() + getMonthDays() ) / 7 );
                    break;

                case КНОПКА_ВНИЗ:
                    Месяц = calendar.get( Calendar.MONTH );
                    if ( Месяц == 11 ) {
                        int Год = calendar.get( Calendar.YEAR );
                        Год += 1;
                        calendar.set( Calendar.YEAR, Год );
                    }
                    Месяц += 1;
                    Месяц %= 12;
                    calendar.set( Calendar.DAY_OF_MONTH, 1 );
                    calendar.set( Calendar.MONTH, Месяц );
                    currentTimestamp = getSelectedDate().getTime();
                    weeks = ( int ) Math.ceil( ( ( double ) getStartWeekday() + getMonthDays() ) / 7 );
                    break;

                case КНОПКА_ВПРАВО:
                    break;

                case КНОПКА_ВЛЕВО:
                    break;
            }
        }
    }

    class ОбразОбластиНедель {
        private int ОтступСверху;
        private int ОтступСлева;
        private int Ширина;
        private int Высота;

        ОбразОбластиНедель( int ОтступСверху, int ОтступСлева ) {
            this.ОтступСверху = ОтступСверху;
            this.ОтступСлева = ОтступСлева;
            Ширина = ШиринаЯчейки * 2 - 4;
            Высота = ВысотаЯчейки * 6 - 4;
        }

        public void Нарисовать( Graphics Холст, boolean Выделен ) {
            int СтарыйЦвет;

            if ( Выделен ) {
                СтарыйЦвет = Холст.getColor();
                Холст.setColor( 10, 80, 200 );
                Холст.setStrokeStyle( Graphics.SOLID );
                Холст.drawRect( ОтступСлева, ОтступСверху, Ширина, Высота );
                Холст.setColor( СтарыйЦвет );
            } else {
                Холст.setStrokeStyle( Graphics.DOTTED );
                Холст.drawRect( ОтступСлева, ОтступСверху, Ширина, Высота );
                Холст.setStrokeStyle( Graphics.SOLID );
            }

            Font Шрифт = Font.getDefaultFont();
            int ШиринаТекста = Шрифт.stringWidth( "Н00" );
            int ВысотаТекста = Шрифт.getHeight();

            for ( int ii = 0; ii < weeks; ii++ ) {
                СтарыйЦвет = Холст.getColor();

                if ( Выделен && ( ii == НомерНедели ) ) {
                    int N = ( ( Integer ) НомераЗаписейРасписаний.elementAt( НомерРасписания ) ).intValue();
                    Холст.setColor( ЦветРасписанияN(N) );

                    Холст.fillRect( ОтступСлева + 2,
                            ОтступСверху + ii * Высота / 6 + 2,
                            Ширина - 3, Высота / 6 - 3 );
                    Холст.setColor( ЦВЕТ_КРАСНЫЙ );
                }

                Холст.drawRect( ОтступСлева + 1,
                        ОтступСверху + ii * Высота / 6 + 1,
                        Ширина - 2, Высота / 6 - 2 );
                Холст.drawString( "Н0" + Integer.toString(ii),
                        ОтступСлева + ( Ширина - ШиринаТекста ) / 2 ,
                        ОтступСверху + ii * Высота / 6 + ( Высота / 6 - ВысотаТекста ) / 2 ,
                        Graphics.TOP | Graphics.LEFT );
                Холст.setColor( СтарыйЦвет );
            }
        }

        public void ОбработкаКлавиш( int Кнопка ) {
            switch( Кнопка ) {
                case КНОПКА_ВВОД:
                    int ДеньНачала = getStartWeekday();
                    int КолвоДней = getMonthDays();
                    int нач, кон;

                    нач = ( НомерНедели == 0 ) ? 0 : НомерНедели * 7 - ДеньНачала;
                    кон = ( НомерНедели == 0 ) ? 7 - ДеньНачала : нач + 7;
                    if ( кон > КолвоДней ) кон = КолвоДней;

                    int месяц = calendar.get( Calendar.MONTH );
                    int N;

                    Schedules.ОбразПлана План;
                    План = ( Schedules.ОбразПлана ) МенеджерРасписаний.СписокПланов.Элементы.elementAt( НомерПлана );
                    N = ( ( Integer ) НомераЗаписейРасписаний.elementAt( НомерРасписания ) ).intValue();

                    for ( int ii = нач; ii < кон; ii ++ ) {
                        switch ( ТипПлана ) {
                            case ПЛАН_ОСНОВНОЙ:
                                План.Основной[ месяц ][ii] = new Integer(N);
                                break;
                            case ПЛАН_ДОПОЛНИТЕЛЬНЫЙ:
                                План.Дополнительный[ месяц ][ii] = new Integer(N);
                                break;
                        }
                    }
                    break;

                case КНОПКА_ВВЕРХ:
                    if ( НомерНедели == 0 ) НомерНедели = weeks;
                    НомерНедели -= 1;
                    break;

                case КНОПКА_ВНИЗ:
                    НомерНедели += 1;
                    НомерНедели %= weeks;
                    break;

                case КНОПКА_ВПРАВО:
                    break;

                case КНОПКА_ВЛЕВО:
                    break;
            }
        }
    }

    class ОбразОбластиДней {
        private int ОтступСверху;
        private int ОтступСлева;
        private int Ширина;
        private int Высота;

        ОбразОбластиДней( int ОтступСверху, int ОтступСлева ) {
            this.ОтступСверху = ОтступСверху;
            this.ОтступСлева = ОтступСлева;
            Ширина = ШиринаЯчейки * 6 + ШиринаЯчейки / 2 - 4;
            Высота = ВысотаЯчейки * 1 - 4;
        }

        public void Нарисовать( Graphics Холст, boolean Выделен ) {
            int СтарыйЦвет;

            if ( Выделен ) {
                СтарыйЦвет = Холст.getColor();
                Холст.setColor( 10, 80, 200 );
                Холст.setStrokeStyle( Graphics.SOLID );
                Холст.drawRect( ОтступСлева, ОтступСверху, Ширина, Высота );
                Холст.setColor( СтарыйЦвет );
            } else {
                Холст.setStrokeStyle( Graphics.DOTTED );
                Холст.drawRect( ОтступСлева, ОтступСверху, Ширина, Высота );
                Холст.setStrokeStyle( Graphics.SOLID );
            }

            Font Шрифт = Font.getDefaultFont();
            int ШиринаТекста = Шрифт.stringWidth( WEEKDAY_LABELS[0] );
            int ВысотаТекста = Шрифт.getHeight();

            for ( int ii = 0; ii < 7; ii++ ) {
                СтарыйЦвет = Холст.getColor();

                if ( Выделен && ( ii == ДеньНедели ) ) {
                    int N = ( ( Integer ) НомераЗаписейРасписаний.elementAt( НомерРасписания ) ).intValue();
                    Холст.setColor( ЦветРасписанияN(N) );

                    Холст.fillRect( ОтступСлева + ( ii * Ширина ) / 7 + 2,
                            ОтступСверху + 2,
                            Ширина / 7 - 3, Высота - 3 );
                    Холст.setColor( ЦВЕТ_КРАСНЫЙ );
                }

                Холст.drawRect( ОтступСлева + ( ii * Ширина ) / 7 + 1,
                        ОтступСверху + 1,
                        Ширина / 7 - 2, Высота - 2 );
                Холст.drawString( WEEKDAY_LABELS[ii],
                        ОтступСлева + ii * Ширина / 7 + ( Ширина / 7 - ШиринаТекста ) / 2 ,
                    ОтступСверху + ( Высота - ВысотаТекста ) / 2,
                    Graphics.TOP | Graphics.LEFT );
                Холст.setColor( СтарыйЦвет );
            }
        }

        public void ОбработкаКлавиш( int Кнопка ) {
            switch( Кнопка ) {
                case КНОПКА_ВВОД:
                    int ДеньНачала = getStartWeekday();
                    int нач = 0;
                    if ( ДеньНедели == ДеньНачала ) нач = 0;
                    if ( ДеньНедели > ДеньНачала ) нач = ДеньНедели - ДеньНачала;
                    if ( ДеньНедели < ДеньНачала ) нач = 7 - ( ДеньНачала - ДеньНедели );

                    int КолвоДней = getMonthDays();
                    int месяц = calendar.get( Calendar.MONTH );
                    int N;

                    Schedules.ОбразПлана План;
                    План = ( Schedules.ОбразПлана ) МенеджерРасписаний.СписокПланов.Элементы.elementAt( НомерПлана );
                    N = ( ( Integer ) НомераЗаписейРасписаний.elementAt( НомерРасписания ) ).intValue();

                    for ( int ii = нач; ii < КолвоДней; ii += 7 ) {
                        switch ( ТипПлана ) {
                            case ПЛАН_ОСНОВНОЙ:
                                План.Основной[ месяц ][ii] = new Integer(N);
                                break;
                            case ПЛАН_ДОПОЛНИТЕЛЬНЫЙ:
                                План.Дополнительный[ месяц ][ii] = new Integer(N);
                                break;
                        }
                    }
                    break;

                case КНОПКА_ВВЕРХ:
                    int Месяц = calendar.get( Calendar.MONTH );
                    if ( Месяц == 0 ) {
                        int Год = calendar.get( Calendar.YEAR );
                        Год -= 1;
                        calendar.set( Calendar.YEAR, Год );
                        Месяц = 12;
                    }
                    Месяц -= 1;
                    calendar.set( Calendar.DAY_OF_MONTH, 1 );
                    calendar.set( Calendar.MONTH, Месяц );
                    currentTimestamp = getSelectedDate().getTime();
                    weeks = ( int ) Math.ceil( ( ( double ) getStartWeekday() + getMonthDays() ) / 7 );
                    break;

                case КНОПКА_ВНИЗ:
                    Месяц = calendar.get( Calendar.MONTH );
                    if ( Месяц == 11 ) {
                        int Год = calendar.get( Calendar.YEAR );
                        Год += 1;
                        calendar.set( Calendar.YEAR, Год );
                    }
                    Месяц += 1;
                    Месяц %= 12;
                    calendar.set( Calendar.DAY_OF_MONTH, 1 );
                    calendar.set( Calendar.MONTH, Месяц );
                    currentTimestamp = getSelectedDate().getTime();
                    weeks = ( int ) Math.ceil( ( ( double ) getStartWeekday() + getMonthDays() ) / 7 );
                    break;

                case КНОПКА_ВПРАВО:
                    ДеньНедели += 1;
                    ДеньНедели %= 7;
                    break;

                case КНОПКА_ВЛЕВО:
                    if ( ДеньНедели == 0 ) ДеньНедели = 7;
                    ДеньНедели -= 1;
                    break;
            }
        }
    }

    class ОбразОбластиЧисел {
        private int ОтступСверху;
        private int ОтступСлева;
        private int Ширина;
        private int Высота;

        ОбразОбластиЧисел( int ОтступСверху, int ОтступСлева ) {
            this.ОтступСверху = ОтступСверху;
            this.ОтступСлева = ОтступСлева;
            Ширина = ШиринаЯчейки * 6 + ШиринаЯчейки / 2 - 4;
            Высота = ВысотаЯчейки * 6 - 4;
        }

        public void Нарисовать( Graphics Холст, boolean Выделен ) {
            if ( Выделен ) {
                int СтарыйЦвет = Холст.getColor();
                Холст.setColor( 10, 80, 200 );
                Холст.setStrokeStyle( Graphics.SOLID );
                Холст.drawRect( ОтступСлева, ОтступСверху, Ширина, Высота );
                Холст.setColor( СтарыйЦвет );
            } else {
                Холст.setStrokeStyle( Graphics.DOTTED );
                Холст.drawRect( ОтступСлева, ОтступСверху, Ширина, Высота );
                Холст.setStrokeStyle( Graphics.SOLID );
            }

            int КолвоДней = getMonthDays();
            int dayIndex = ( getStartWeekday() - startWeekday + 7 ) % 7;

            Холст.setColor( 0, 0, 0 );

            int Сегодня = calendar.get( Calendar.DAY_OF_MONTH );

            Font Шрифт = Font.getDefaultFont();
            int ШиринаТекста = Шрифт.stringWidth( "00" );
            int ВысотаТекста = Шрифт.getHeight();

            int weekday, row, x, y, N, месяц, число;
            Schedules.ОбразПлана План;
            int СтарыйЦвет;

            План = ( Schedules.ОбразПлана ) МенеджерРасписаний.СписокПланов.Элементы.elementAt( НомерПлана );

            for ( int i = 0; i < КолвоДней; i++ ) {
                weekday = ( dayIndex + i ) % 7;
                row = ( dayIndex + i ) / 7;

                x = ОтступСлева + ( weekday * Ширина ) / 7 + ( Ширина / 7 - ШиринаТекста ) / 2;
                y = ОтступСверху + ( row * Высота ) / 6 + ( Высота / 6 - ВысотаТекста ) / 2;

                if ( Выделен && ( ( i + 1 ) == Сегодня ) ) {
                    СтарыйЦвет = Холст.getColor();

                    N = ( ( Integer ) НомераЗаписейРасписаний.elementAt( НомерРасписания ) ).intValue();
                    Холст.setColor( ЦветРасписанияN(N) );
                    Холст.fillRect( ОтступСлева + ( weekday * Ширина ) / 7 + 2,
                            ОтступСверху + ( row * Высота ) / 6 + 2,
                            Ширина / 7 - 3, Высота / 6 - 3 );

                    Холст.setColor( ЦВЕТ_КРАСНЫЙ );
                    Холст.drawRect( ОтступСлева + ( weekday * Ширина ) / 7 + 1,
                            ОтступСверху + ( row * Высота ) / 6 + 1,
                            Ширина / 7 - 2, Высота / 6 - 2 );

                    Холст.drawString( ( ( i + 1 ) < 10 ? ( " " + ( i + 1 ) ) : ( "" + ( i + 1 ) ) ),
                            x, y, Graphics.TOP | Graphics.LEFT );

                    Холст.setColor( СтарыйЦвет );
                } else {
                    СтарыйЦвет = Холст.getColor();

                    месяц = calendar.get( Calendar.MONTH );
                    число = i;

                    switch ( ТипПлана ) {
                        case ПЛАН_ОСНОВНОЙ:
                            N = План.Основной[ месяц ][ число ].intValue();
                            break;
                        case ПЛАН_ДОПОЛНИТЕЛЬНЫЙ:
                            N = План.Дополнительный[ месяц ][ число ].intValue();
                            break;
                        default:
                            N = План.Основной[ месяц ][ число ].intValue();
                    }

                    Холст.setColor( ЦветРасписанияN( N ) );
                    Холст.fillRect( ОтступСлева + ( weekday * Ширина ) / 7 + 2,
                            ОтступСверху + ( row * Высота ) / 6 + 2,
                            Ширина / 7 - 3, Высота / 6 - 3 );

                    Холст.setColor( СтарыйЦвет );

                    Холст.drawRect( ОтступСлева + ( weekday * Ширина ) / 7 + 1,
                            ОтступСверху + ( row * Высота ) / 6 + 1,
                            Ширина / 7 - 2, Высота / 6 - 2 );

                    Холст.drawString( ( ( i + 1 ) < 10 ? ( " " + ( i + 1 ) ) : ( "" + ( i + 1 ) ) ),
                            x, y, Graphics.TOP | Graphics.LEFT );

                    //if this is the current day, we must restore standard fore color
                    if ( ( i + 1 ) == Сегодня ) {
                        Холст.setColor( 0, 0, 0 );
                    }
                }
            }

            Холст.setColor( 0, 0, 0 );
        }

        public void ОбработкаКлавиш( int Кнопка ) {
            switch( Кнопка ) {
                case КНОПКА_ВВОД:
                    int месяц = calendar.get( Calendar.MONTH );
                    int число = calendar.get( Calendar.DAY_OF_MONTH ) - 1;
                    int N;
                    Schedules.ОбразПлана План;
                    План = ( Schedules.ОбразПлана ) МенеджерРасписаний.СписокПланов.Элементы.elementAt( НомерПлана );
                    N = ( ( Integer ) НомераЗаписейРасписаний.elementAt( НомерРасписания ) ).intValue();

                    switch ( ТипПлана ) {
                        case ПЛАН_ОСНОВНОЙ:
                            План.Основной[ месяц ][ число ] = new Integer(N);
                            break;
                        case ПЛАН_ДОПОЛНИТЕЛЬНЫЙ:
                            План.Дополнительный[ месяц ][ число ] = new Integer(N);
                            break;
                    }
                    break;
                case КНОПКА_ВВЕРХ:
                    go(-7);
                    currentTimestamp = getSelectedDate().getTime();
                    break;
                case КНОПКА_ВНИЗ:
                    go(7);
                    currentTimestamp = getSelectedDate().getTime();
                    break;
                case КНОПКА_ВПРАВО:
                    go(1);
                    currentTimestamp = getSelectedDate().getTime();
                    break;
                case КНОПКА_ВЛЕВО:
                    go(-1);
                    currentTimestamp = getSelectedDate().getTime();
                    break;
            }
        }
    }

    class ОбразОбластиСтатуса {
        private int ОтступСверху;
        private int ОтступСлева;
        private int Ширина;
        private int Высота;

        private String Текст;

        ОбразОбластиСтатуса( int ОтступСверху, int ОтступСлева, String Текст ) {
            this.ОтступСверху = ОтступСверху;
            this.ОтступСлева = ОтступСлева;
            this.Текст = Текст;
            Ширина = ШиринаЯчейки * 8 + ШиринаЯчейки / 2 - 4;
            Высота = ВысотаЯчейки * 1 - 4;
}

        public void Нарисовать( Graphics Холст ) {
            Холст.setStrokeStyle( Graphics.DOTTED );
            Холст.drawRect( ОтступСлева, ОтступСверху, Ширина, Высота );
            Холст.setStrokeStyle( Graphics.SOLID );

            Font Шрифт = Font.getDefaultFont();
            int ВысотаТекста = Шрифт.getHeight();

            Холст.drawString( Текст, ОтступСлева,
                    ОтступСверху + ( Высота - ВысотаТекста ) / 2,
                    Graphics.TOP | Graphics.LEFT );
        }
    }

    // <editor-fold desc=" Конструктор ">

    public Planner( MIDlet Мидлет, Displayable Окно, Schedules Менеджер, Settings Настройки, int НомерПлана, Date date ) {

        this.Мидлет = Мидлет;
        this.Дисплей = Display.getDisplay( Мидлет );
        this.РодительскоеОкно = Окно;
        this.Настройки = Настройки;
        this.МенеджерРасписаний = Менеджер;
        calendar = Calendar.getInstance();

        ВысотаЯчейки = Настройки.Планировщик.ВысотаЯчейки;
        ШиринаЯчейки = Настройки.Планировщик.ШиринаЯчейки;

        ОбластьЗаголовка = new ОбразОбластиЗаголовока( 2, 2, "Планировщик" );

        ОбластьНазвания = new ОбразОбластиНазвания( ВысотаЯчейки + 2, 2 );
        ОбластьТипа = new ОбразОбластиТипа( ВысотаЯчейки * 2 + 2, 2 );
        ОбластьГода = new ОбразОбластиГода( ВысотаЯчейки * 2 + 2, ШиринаЯчейки * 2 + 2 );
        ОбластьМесяца = new ОбразОбластиМесяца( ВысотаЯчейки * 2 + 2, ШиринаЯчейки * 5 + ШиринаЯчейки / 2 + 2 );
        ОбластьЦвета = new ОбразОбластиЦвета( ВысотаЯчейки * 3 + 2, 2 );
        ОбластьВсё = new ОбразОбластиВсё( ВысотаЯчейки * 3 + 2, ШиринаЯчейки * 1 + 2 );
        ОбластьНедель = new ОбразОбластиНедель( ВысотаЯчейки * 4 + 2, 2 );
        ОбластьДней = new ОбразОбластиДней( ВысотаЯчейки * 3 + 2, ШиринаЯчейки * 2 + 2 );
        ОбластьЧисел = new ОбразОбластиЧисел( ВысотаЯчейки * 4 + 2, ШиринаЯчейки * 2 + 2 );
        ОбластьСтатуса = new ОбразОбластиСтатуса( ВысотаЯчейки * 10 + 2, 2, "*, стрелки, ввод, цифры" );


        ВыбранныйЭлемент = ФОКУС_ЧИСЛА;
        ДеньНедели = 0;
        НомерНедели = 0;
        ТипПлана = ПЛАН_ОСНОВНОЙ;
        this.НомерПлана = НомерПлана;

        НомерРасписания = 0;
        КолвоРасписаний = МенеджерРасписаний.Расписания.size();

        НомераЗаписейРасписаний = new Vector();

        for ( int ii = 0; ii < КолвоРасписаний; ii++ ) {
            НомераЗаписейРасписаний.addElement( new Integer( НомерЗаписиРасписанияN(ii) ) );
        }

        setDate( date );

        Schedules.ОбразПлана План;
        План = ( Schedules.ОбразПлана ) МенеджерРасписаний.СписокПланов.Элементы.elementAt( НомерПлана );

        main.Логгер.info( "[Planner.java]: Planner( \"" + План.Название + "\" )" );

    }

    // </editor-fold>

    // <editor-fold desc=" Методы класса ">

    public int ЦветРасписанияN( int Номер ) {

        int Красный, Синий, Зелёный;
        Schedules.ScheduleClass Расписание;

        Random random = new Random( 27182 );
        Record Запись = ( Record ) МенеджерРасписаний.Данные.Записи.elementAt( Номер );

        for ( int ii = 0; ii < МенеджерРасписаний.Расписания.size(); ii++ ) {

            Расписание = ( Schedules.ScheduleClass ) МенеджерРасписаний.Расписания.elementAt(ii);
            Красный = Math.abs( random.nextInt() ) % 255;
            Синий = Math.abs( random.nextInt() ) % 255;
            Зелёный = Math.abs( random.nextInt() ) % 255;

            if ( Запись.Имя.equals( Расписание.Название ) ) {

                if ( Расписание.Уроки.isEmpty() ) return 0xFFFFFF;

                return ( 128 << 24 ) + ( Красный << 16 ) + ( Синий << 8 ) + Зелёный;
            }

        }

        return 0xFFFFFF;
    }


    public int НомерЗаписиРасписанияN( int Номер ) {

        Schedules.ScheduleClass Расписание;
        Record Запись;

        Расписание = ( Schedules.ScheduleClass ) МенеджерРасписаний.Расписания.elementAt( Номер );

        for ( int ii = 0; ii < МенеджерРасписаний.Данные.Записи.size(); ii++ ) {

            Запись = ( Record ) МенеджерРасписаний.Данные.Записи.elementAt( ii );

            if ( ( Запись.ТипЗаписи == Schedules.ТИП_РАСПИСАНИЕ )
                    && ( Расписание.Название.equals( Запись.Имя ) ) ) {

                return ii;
            }

        }


        return -1;
    }


    public void СменитьФокус() {

        ВыбранныйЭлемент += 1;
        ВыбранныйЭлемент %= ФОКУС_ЧИСЛА + 1;
    }


    public Date getSelectedDate() {

        return calendar.getTime();
    }


    public void setDate( Date Дата ) {

        currentTimestamp = Дата.getTime();

        calendar.setTime( Дата );

        //weeks number can change, depending on week starting day and month total days
        weeks = ( int ) Math.ceil( ( ( double ) getStartWeekday() + getMonthDays() ) / 7 );
    }


    public void setDate( long timestamp ) {

        setDate( new Date( timestamp ) );
    }


    int getMonthDays() {

        int month = calendar.get( Calendar.MONTH );

        switch(month)
        {
        case 3:
        case 5:
        case 8:
        case 10:
                return 30;
        case 1:
                return calendar.get(Calendar.YEAR) % 4 == 0 && calendar.get(Calendar.YEAR) % 100 != 0 ? 29 : 28;
        default:
                return 31;
        }

    }


    int getStartWeekday() {

        //let's create a new calendar with same month and year, but with day 1
        Calendar c = Calendar.getInstance();

        c.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        c.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
        c.set(Calendar.DAY_OF_MONTH, 1);

        //we must normalize DAY_OF_WEEK returned value
        return (c.get(Calendar.DAY_OF_WEEK) + 5) % 7;
    }


    void go( int delta ) {

        int prevMonth = calendar.get( Calendar.MONTH );

        setDate( currentTimestamp + 86400000 * delta );

        //we have to check if month has changed
        //if yes, we have to recalculate month height
        //since weeks number could be changed
        if ( calendar.get( Calendar.MONTH ) != prevMonth ) {

        }

    }

    // </editor-fold>

    // <editor-fold desc=" Обработчики событий ">

    public void keyPressed( int Кнопка ) {

        switch ( ВыбранныйЭлемент ) {

            case ФОКУС_ПЛАН:

                ОбластьНазвания.ОбработкаКлавиш( Кнопка );
                break;

            case ФОКУС_ТИП:

                ОбластьТипа.ОбработкаКлавиш( Кнопка );
                break;

            case ФОКУС_ГОД:

                ОбластьГода.ОбработкаКлавиш( Кнопка );
                break;

            case ФОКУС_МЕСЯЦ:

                ОбластьМесяца.ОбработкаКлавиш( Кнопка );
                break;

            case ФОКУС_ЦВЕТ:

                if ( Кнопка == КНОПКА_ВВОД ) {

                    ( new SelectScheduleColorForm( Мидлет, РодительскоеОкно, МенеджерРасписаний, this  ) ).Отобразить();

                } else {

                    ОбластьЦвета.ОбработкаКлавиш( Кнопка );
                }
                break;

            case ФОКУС_ВЫДЕЛИТЬ_ВСЁ:

                ОбластьВсё.ОбработкаКлавиш( Кнопка );
                break;

            case ФОКУС_НЕДЕЛИ:

                ОбластьНедель.ОбработкаКлавиш( Кнопка );
                break;

            case ФОКУС_ДНИ:

                ОбластьДней.ОбработкаКлавиш( Кнопка );
                break;

            case ФОКУС_ЧИСЛА:

                ОбластьЧисел.ОбработкаКлавиш( Кнопка );
                break;
        }
    }


    public void paint( Graphics Холст ) {

        Холст.setColor( 0 );

        ОбластьЗаголовка.Нарисовать( Холст );
        ОбластьНазвания.Нарисовать( Холст, ( ВыбранныйЭлемент == ФОКУС_ПЛАН ) ? true : false );
        ОбластьТипа.Нарисовать( Холст, ( ВыбранныйЭлемент == ФОКУС_ТИП ) ? true : false );
        ОбластьГода.Нарисовать( Холст, ( ВыбранныйЭлемент == ФОКУС_ГОД ) ? true : false );
        ОбластьМесяца.Нарисовать( Холст, ( ВыбранныйЭлемент == ФОКУС_МЕСЯЦ ) ? true : false );
        ОбластьЦвета.Нарисовать( Холст, ( ВыбранныйЭлемент == ФОКУС_ЦВЕТ ) ? true : false );
        ОбластьВсё.Нарисовать( Холст, ( ВыбранныйЭлемент == ФОКУС_ВЫДЕЛИТЬ_ВСЁ ) ? true : false );
        ОбластьНедель.Нарисовать( Холст, ( ВыбранныйЭлемент == ФОКУС_НЕДЕЛИ ) ? true : false );
        ОбластьДней.Нарисовать( Холст, ( ВыбранныйЭлемент == ФОКУС_ДНИ ) ? true : false );
        ОбластьЧисел.Нарисовать( Холст, ( ВыбранныйЭлемент == ФОКУС_ЧИСЛА ) ? true : false );
        ОбластьСтатуса.Нарисовать( Холст );

    }

    // </editor-fold>
    
}
