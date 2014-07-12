
// <editor-fold desc=" Подключаемые модули ">

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.midlet.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;

import net.sf.microlog.core.*;
import net.sf.microlog.core.appender.*;
import net.sf.microlog.core.format.*;
import net.sf.microlog.midp.file.FileAppender;

// </editor-fold>

public class main extends MIDlet {

    // Установка или сброс для отладочного режима.
    static final public boolean РЕЖИМ_СИМУЛЯТОРА = false;

    public static Logger Логгер = null;
    public static Player Плеер = null;

    public Settings Настройки = null;


    public static void ОбновитьПлеер( Player Плеер ) throws MediaException {

       if ( Плеер != null ) {

            if ( Плеер.getState() == Player.STARTED ) {

                Плеер.stop();

            }

            if ( Плеер.getState() == Player.PREFETCHED ) {

               Плеер.deallocate();

            }

            if ( Плеер.getState() == Player.REALIZED ||
                Плеер.getState() == Player.UNREALIZED ) {

                Плеер.close();
            }

        }

        Плеер = null;

    }


    public static void ПроигратьФайл( PlayerListener Обработчик, String Путь, int Громкость ) {

        try {

            // Пустая строка - пропускаем.
            if ( Путь.trim().length() == 0 ) return;

            String ФорматПотока = Utils.guessContentType( Путь );

            FileConnection Файл = ( FileConnection ) Connector.open( Путь );

            if ( ( Файл == null ) || !Файл.exists() ) {

                Файл.close();
                throw new Exception( "Файл \"" + Файл + "\" не существует." );
            }

            if ( Файл.isDirectory() ) {

                Файл.close();
                return;
            }

            InputStream ВходнойПоток = Файл.openInputStream();

            ОбновитьПлеер( Плеер );

            Плеер = Manager.createPlayer( ВходнойПоток, ФорматПотока );

            if ( Плеер == null ) {

                ВходнойПоток.close();
                Файл.close();

                throw new Exception( "Ошибка при запуске плеера." );
            }

            Плеер.addPlayerListener( Обработчик );
            Плеер.realize();

            VolumeControl Регулятор = ( VolumeControl ) Плеер.getControl( "VolumeControl" );

            if ( Регулятор != null ) Регулятор.setLevel( Громкость );

            // Предварительная загрузка данных.
            Плеер.prefetch();
            Плеер.start();

            ВходнойПоток.close();
            Файл.close();

        } catch ( MediaException МедиаИсключение ) {

            Логгер.error( "[main.java]: ПроигратьФайл(): Ошибка при запуске плеера. "
                    + МедиаИсключение.getMessage() );

        } catch ( IOException ИсключениеВводаВывода ) {

            Логгер.error( "[main.java]: ПроигратьФайл(): Ошибка при вводе/выводе. "
                    + ИсключениеВводаВывода.getMessage() );

        } catch ( Exception Исключение ) {

            Логгер.error( "[main.java]: ПроигратьФайл(): " + Исключение.getMessage() );
        }

    }


    // Точка входа в программу.
    public void startApp() {

        // Создаём экземпляр объекта настроек и журнала сообщений.
        Настройки = new Settings( this );

        // Пытаемся восстановить настройки из хранилища.
        Настройки.ЗагрузитьНастройки( Настройки.Основные.ИмяВХранилище );

        // Настраиваем журнал сообщений.
        Логгер = LoggerFactory.getLogger();

        Логгер.setLevel( Level.DEBUG );

//        %c - prints the name of the Logger
//        %d - prints the date (absolute time)
//        %m - prints the logged message
//        %P -prints the priority, i.e. Level of the message.
//        %r - prints the relative time of the logging. (The first logging is done at time 0.)
//        %t - prints the thread name.
//        %T - prints the Throwable object.
//        %% - prints the '%' sign.

        //PropertyConfigurator.configure( "/microlog.properties" );
        PatternFormatter ФормировательШаблона = new PatternFormatter();

//        ФормировательШаблона.setPattern( "%c %d [%P] %m %T" );
        ФормировательШаблона.setPattern( "%d [%P] %m" );

        ConsoleAppender НаполнительКонсоли = new ConsoleAppender();
        НаполнительКонсоли.setFormatter( ФормировательШаблона );

        FileAppender НаполнительФайла = new FileAppender();
        НаполнительФайла.setFileName( Настройки.Основные.ФайлЛога );

        try {

            FileConnection файл = ( FileConnection ) Connector.open( Настройки.Основные.ФайлЛога, Connector.READ_WRITE );

            // Удаляем файл при превышении заданного ограничения.
            if ( файл.fileSize() > ( Настройки.Основные.РазмерФайлаЛога * 1024 ) ) файл.delete();

            файл.close();

            НаполнительФайла.setAppend( true );
            НаполнительФайла.setFormatter( ФормировательШаблона );

            Логгер.addAppender( НаполнительКонсоли );
            Логгер.addAppender( НаполнительФайла );

        } catch ( IOException ИсключениеВводаВывода ) {

            Логгер.addAppender( НаполнительКонсоли );

            Логгер.error( "[Settings.java]: Ошибка создания файла лога."
                    + ИсключениеВводаВывода.getMessage() );
        }

        // Отображаем основную форму.
        ( new MenuForm( this, Настройки ) ).Отобразить();
    }


    public void pauseApp() {
    }


    public void destroyApp( boolean unconditional ) {
    }

}
