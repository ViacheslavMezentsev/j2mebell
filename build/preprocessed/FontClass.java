// Основан на классе FontClass от 16.06.2008, автор: magdelphi (magdelphi@rambler.ru)

// <editor-fold defaultstate="collapsed" desc=" Подключаемые модули ">

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

// </editor-fold>


public class FontClass {

    // <editor-fold defaultstate="collapsed" desc=" Поля класса ">

    private boolean Доступен = false;

    private int Ширина = 0;
    private int ТекущийЦвет = 0;

    // высота символов.
    private int h_char;

    // флаг стиля символов italic.
    private int Курсив = 0;

    // данные  таблицы символов из файла xxxxx.dat
    private byte buff[] = new byte[768];

    // данные одного символа.
    private static int[] buf;
    private static Image ОбразШрифта = null;

    // </editor-fold>

    // <editor-fold desc=" Конструктор ">

    public FontClass( String ИмяШрифта ) {

        int off = 0;
        int readBytes;
        int n_buf;

        try {

            // Загрузка образа символов.
            ОбразШрифта = Image.createImage( "/" + ИмяШрифта + ".png" );

            // Загрузка таблицы расположения символов.
            InputStream is = getClass().getResourceAsStream( "/" + ИмяШрифта + ".dat" );

            // копируем в буфер.
            while ( is.read( buff, off, buff.length ) > -1 ) {}

            is.close();

            // высота символов.
            h_char = buff[0];

            // если fontstyle = [italic] увеличиваем ширину символа.
            if ( buff[1] == 1 ) Курсив = h_char / 4;

            // кол-во байт 1 знакоместо.
            n_buf = h_char * h_char;
            buf = new int[ n_buf ];

            Доступен = true;

        } catch ( Exception Исключение ) {

            main.Логгер.error( "[FontClass.java]: " + Исключение.toString() );
        }

    }

    // </editor-fold>

    // <editor-fold desc=" Методы класса ">

    // Возвращает значение цвета из составляющих a-фльфа, RGB.
    private int toBGR( int Прозрачность, int Красный, int Зелёный, int Синий ) {

        Прозрачность = ( Прозрачность << 24 );// & 0xFF000000;
        Красный = ( Красный << 16 );// & 0x00FF0000;
        Зелёный = ( Зелёный << 8 );// & 0x0000FF00;

        return ( Прозрачность | Красный | Зелёный | Синий );
    }


    // Устанавливает текущий цвет отображения букв по составляющим  a-aфльфа, RGB.
    public void УстановитьЦвет( int Прозрачность, int Красный, int Зелёный, int Синий ) {

        ТекущийЦвет = toBGR( Прозрачность, Красный, Зелёный, Синий );
    }


    // Устанавливает текущий цвет отображения букв по составляющим  a-aфльфа, RGB.
    public void УстановитьЦвет( int Цвет ) {

        ТекущийЦвет = Цвет;
    }


    // Выводит на экран один символ.
    public int ОтобразитьСимвол( Graphics Холст, char Символ, int Слева, int Сверху ) {

        int Результат = 0;

        if ( Доступен ) {

            String s = String.valueOf( Символ );

            // unicode to ansi
            int ch = s.charAt(0) ;
            ch = ch == 0x400 ? 0xa7 : ch == 0x450 ? 0xb7 : ch;
            ch = ( ch > 0x400 ) ? ( ch - 0x350 ) : ch;

            // смещение данных в таблице xxxxx.dat
            int ind = ( ( int ) (ch) - 0x20 ) * 3;

            // смещение в таблице xxxxx.png
            int len = 0;

            // старший байт
            int hlen = ( buff[ ind + 1 ] & 0x00ff ) << 8;

            // смещение в таблице xxxxx.png
            len = ( buff[ ind ] & 0x00ff ) + hlen;

            // ширина символа
            int width_char = buff[ ind + 2 ] + Курсив;

            // считать в буфер
            ОбразШрифта.getRGB( buf, 0, width_char, len - 2, 0, width_char, h_char );

            int imageSize = width_char * h_char;

            for ( int i = 0; i < buf.length; i++ ) {

                // читаем только RGB
                int color = ( buf[i] & 0x00ffffff );

                // если черный красим в цвет
                if ( color == 0 ) color =  ТекущийЦвет;

                buf[i] = color;

            }

            Холст.drawRGB( buf, 0, width_char, Слева, Сверху, width_char, h_char, true );

            if ( Символ == ' ' ) width_char = h_char >> 2;

            Результат = width_char;

        }

        return Результат;
    }


    // Выводит строку символов.
    public void ОтобразитьСтроку( Graphics Холст, String Строка, int Слева, int Сверху ) {

        int len = Слева;
        int max_width = Холст.getClipWidth();

        for ( int i = 0; i < Строка.length(); i++ ) {

            len += ОтобразитьСимвол( Холст, Строка.charAt(i), len, Сверху );

            //if ( len >= max_width - 1 ) return;

        }

        Ширина = len;
    }


    // Удаление объектов.
    public void Очистить() {

        buff = null;
        buf = null;
        ОбразШрифта = null;

    }

    // </editor-fold>

}
