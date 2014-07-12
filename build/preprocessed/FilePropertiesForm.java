
// <editor-fold defaultstate="collapsed" desc=" Подключаемые модули ">

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

// </editor-fold>

public class FilePropertiesForm implements CommandListener {

    // <editor-fold defaultstate="collapsed" desc=" Поля класса ">

    private Command Назад = new Command ( "Назад", Command.BACK, 0 );
    private Image yesIcon;
    private Image noIcon;

    private MIDlet Мидлет;
    private Display Дисплей;
    private Displayable РодительскоеОкно;

    private Form Свойства;

    // </editor-fold>

    // <editor-fold desc=" Конструктор ">

    public FilePropertiesForm( MIDlet Мидлет, Displayable окно, String ИмяПапки, String ИмяФайла ) {

        this.Мидлет = Мидлет;
        Дисплей = Display.getDisplay( Мидлет );
        РодительскоеОкно = окно;

        try {
            yesIcon = Image.createImage( "/icons/yes.png" );
            noIcon = Image.createImage( "/icons/no.png" );

        } catch ( IOException ex ) {

            main.Логгер.error( "[FilePropertiesForm.java]: Иконки не загружены: " + ex.getMessage() );

            yesIcon = null;
            noIcon = null;
        }

        try {

            FileConnection Файл = ( FileConnection ) Connector.open( "file:///" + ИмяПапки + ИмяФайла );

            if ( !Файл.exists() ) {

                throw new IOException ( "Файл не существует." );
            }

            Свойства = new Form( "Свойства файла" );

            Свойства.append( "Имя: " + ИмяФайла + "\n" );
            Свойства.append( "Папка: " + ИмяПапки + "\n" );
            Свойства.append( "Тип: " + ( Файл.isDirectory () ? "Папка" : "Файл" ) + "\n" );
            Свойства.append( "Размер: " + Integer.toString( ( int ) Файл.fileSize() ) + " байт\n"  );
            Свойства.append( "Дата: " + DateToString( Файл.lastModified() ) + "\n" );

            Свойства.append( "Атрибуты:\n" );
            Свойства.append( Файл.canRead() ? yesIcon : noIcon );
            Свойства.append( "Чтение " );
            Свойства.append( Файл.canWrite() ? yesIcon : noIcon );
            Свойства.append( "Запись " );
            Свойства.append( Файл.isHidden() ? yesIcon : noIcon );
            Свойства.append( "Скрытый" );

            Свойства.addCommand( Назад );
            Свойства.setCommandListener( this );

            Файл.close();

            main.Логгер.info( "[FilePropertiesForm.java]: FilePropertiesForm()" );

        } catch ( Exception Исключение ) {

            String Text =  "Нет доступа к файлу " + ИмяФайла + " в папке " + ИмяПапки + "\nИсключение: " + Исключение.getMessage();

            main.Логгер.error( Text );

            Alert alert = new Alert( "Сообщение", Text, null, AlertType.ERROR );

            alert.setTimeout ( Alert.FOREVER );

            Дисплей.setCurrent( alert );
        }

    }

    // </editor-fold>

    // <editor-fold desc=" Методы класса ">

    public void Отобразить() {

        Дисплей.setCurrent( Свойства );
    }

    private String DateToString( long Дата ) {

        Calendar Календарь = Calendar.getInstance();
        String Текст;
        String Неделя[] = { "Вс", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб" };
        String ИмяМесяца[] = { "Янв", "Фев", "Мар", "Апр", "Май", "Июн",
            "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек" };

        Календарь.setTime( new Date( Дата ) );

        int ДеньНедели = Календарь.get( Calendar.DAY_OF_WEEK );
        int Год = Календарь.get( Calendar.YEAR );
        int Месяц = Календарь.get( Calendar.MONTH );
        int Число = Календарь.get( Calendar.DAY_OF_MONTH );
        int Минуты = Календарь.get( Calendar.MINUTE );
        int Часы = Календарь.get( Calendar.HOUR_OF_DAY );
        int Секунды = Календарь.get( Calendar.SECOND );

        Текст = Неделя[ ДеньНедели - 1 ]
            + " "
            + Число
            + " "
            + ИмяМесяца[ Месяц ]
            + " "
            + Год
            + " г.";
        Текст += " ";
        Текст += ( ( Часы < 10 ) ? "0" : "" ) + Часы
            + ":"
            + ( ( Минуты < 10) ? "0" : "" ) + Минуты
            + ":"
            + ( ( Секунды < 10 ) ? "0" : "" ) + Секунды;

        return Текст;

    }

    // </editor-fold>

    // <editor-fold desc=" Обработчики событий ">

    public void commandAction( Command команда, Displayable элемент ) {

        if ( элемент == Свойства ) {

            // Обработка команд
            if ( команда == Назад ) {

                Дисплей.setCurrent( РодительскоеОкно );
            }

        }

    }

    // </editor-fold>

}
