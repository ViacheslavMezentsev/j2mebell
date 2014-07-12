
// <editor-fold defaultstate="collapsed" desc=" Подключаемые модули ">

import java.io.*;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;

// </editor-fold>

public class Settings {

    public class GeneralSettings {

        public String ИмяВХранилище;
        public String МелодииПуть;
        public String ФайлНастроек;
        public String ФайлРасписаний;
        public String ФайлЛога;
        public int РазмерФайлаЛога;
        public int Кодировка;
        public int СуточнаяПоправка;
        public int Громкость;
        public int КолвоСуток;

    }

    public class MonitorSettings {

        public int ШиринаЭкрана;
        public int ВысотаЭкрана;
        public int ОтступXY;
        public int ШагСеткиXY;

        public boolean ПолныйЭкран;

        public int ДиаграммаОтступСверху;
        public int ДиаграммаТолщинаПолосы;
        public int ДиаграммаДлинаИнтервала;

        public int ВремяОтступСверху;
        public int ПланОтступСверху;
        public int РежимОтступСверху;

        public boolean логПоказатьСетку;
        public boolean логПоказатьРамку;
        public boolean логПоказатьДату;
        public boolean логПоказатьВремя;
        public boolean логПоказатьПлан;
        public boolean логПоказатьРежим;
        public boolean логПоказатьДиаграмму;
        public boolean логПоказатьЛегенду;

    }

    public class PlannerSettings {

        public int ШиринаЯчейки;
        public int ВысотаЯчейки;

    }

    public class BellsSettings {

        public int ДлительностьОсновных;
        public int ДлительностьПредварит;
        public int ВремяДоНачала;
        public int ВремяДоОкончания;

        public boolean логПредваритПередНачал;
        public boolean логПредваритПередОконч;

    }

    public class SchedulesSettings {

        public String ИмяЗаписиВХранилище;
        public int Режим;
        public int ТекущийПлан;
        public int ТекущееРасписание;

    }

    // <editor-fold desc=" Поля класса ">

    static final public byte КОДИРОВКА_UTF8 = 0;
    static final public byte КОДИРОВКА_CP1251 = 1;

    static final public String СТРОКА_UTF8 = "UTF-8";
    static final public String СТРОКА_CP1251 = "CP-1251";

    static final public byte РЕЖИМ_СУТОЧНОЕ_РАСПИСАНИЕ = 0;
    static final public byte РЕЖИМ_ГОДОВОЙ_ПЛАН = 1;

    // Размер файла лога в Кб
    static final public int РАЗМЕР_ФАЙЛА_ЛОГА = 100;

    public int Прогресс;

    private MIDlet Мидлет;
    private Display Дисплей;

    private int[] m_arPhoneCharDiv = { (int) 'А' - 192, (int) 'Ё' - 168, (int) 'ё' - 184 };

    public GeneralSettings Основные;
    public MonitorSettings Монитор;
    public PlannerSettings Планировщик;
    public BellsSettings Звонки;
    public SchedulesSettings Расписания;

    // </editor-fold>

    // <editor-fold desc=" Конструктор ">

    Settings( MIDlet Мидлет ) {

        this.Мидлет = Мидлет;
        Дисплей = Display.getDisplay( Мидлет );

        Основные = new GeneralSettings();
        Монитор = new MonitorSettings();
        Планировщик = new PlannerSettings();
        Звонки = new BellsSettings();
        Расписания = new SchedulesSettings();

        УстановитьПоУмолчанию();

    }

    // </editor-fold>

    public void СохранитьНастройки( String ИмяЗаписи ) {

        RecordStore Настройки = null;

        try {

            String[] Список = RecordStore.listRecordStores();

            if ( Список != null ) {

                for ( int ii = 0; ii < Список.length; ii++ ) {

                    if ( ИмяЗаписи.equals( Список[ii] ) ) {

                        RecordStore.deleteRecordStore( ИмяЗаписи );
                    }

                }

            }

            Настройки = RecordStore.openRecordStore( ИмяЗаписи, true );
            byte[] Данные = ВМассивБайт();
            Настройки.addRecord( Данные, 0, Данные.length );
            Настройки.closeRecordStore();

        } catch( RecordStoreException ИсключениеХранилища ) {

            main.Логгер.error( "[Settings.java]: Ошибка при сохранении записи."
                    + ИсключениеХранилища.getMessage() );

        } catch( IOException ИсключениеВводаВывода ) {

            main.Логгер.error( "[Settings.java]: Ошибка при сохранении записи."
                    + ИсключениеВводаВывода.getMessage() );
        }

    }

    public void ЗагрузитьНастройки( String ИмяЗаписи ) {

        RecordStore Настройки = null;

        try {

            String[] Список = RecordStore.listRecordStores();

            if ( Список == null ) return;

            Настройки = RecordStore.openRecordStore( ИмяЗаписи, false );

            if ( Настройки.getNumRecords() > 0 ) {

                byte[] Данные = Настройки.getRecord(1);
                ИзМассиваБайт( Данные );
            }

            Настройки.closeRecordStore();

        } catch( RecordStoreException ИсключениеХранилища ) {

            main.Логгер.error( "[Settings.java]: Ошибка при создании таблицы."
                    + ИсключениеХранилища.getMessage() );

        } catch( IOException ИсключениеВводаВывода ) {

            main.Логгер.error( "[Settings.java]: Ошибка при сохранении списка полей."
                    + ИсключениеВводаВывода.getMessage() );
        }

    }

    public void УстановитьПоУмолчанию() {

        Основные.ИмяВХранилище = "Настройки";

        if ( main.РЕЖИМ_СИМУЛЯТОРА == true ) {

            Основные.МелодииПуть = "file:///root1/";
            Основные.ФайлНастроек = "file:///root1/Мелодии/Настройки.txt";
            Основные.ФайлРасписаний = "file:///root1/Мелодии/Расписания.txt";
            Основные.ФайлЛога = "file:///root1/Мелодии/microlog.txt";

        } else {

            Основные.МелодииПуть = "file:///C:/";
            Основные.ФайлНастроек = "file:///E:/Мелодии/Настройки.txt";
            Основные.ФайлРасписаний = "file:///E:/Мелодии/Расписания.txt";
            Основные.ФайлЛога = "file:///E:/Мелодии/microlog.txt";
        }

        Основные.РазмерФайлаЛога = РАЗМЕР_ФАЙЛА_ЛОГА;
        Основные.СуточнаяПоправка = 0;
        Основные.Кодировка = КОДИРОВКА_UTF8;
        Основные.Громкость = 85;
        Основные.КолвоСуток = 0;

        Монитор.ШиринаЭкрана = 176;
        Монитор.ВысотаЭкрана = 220;
        Монитор.ОтступXY = 1;
        Монитор.ШагСеткиXY = 20;
        Монитор.ПолныйЭкран = true;

        Монитор.ДиаграммаОтступСверху = 120;
        Монитор.ДиаграммаТолщинаПолосы = 15;
        Монитор.ДиаграммаДлинаИнтервала = 2 * 60 + 30; // в минутах;

        Монитор.ВремяОтступСверху = 25;
        Монитор.ПланОтступСверху = 45;
        Монитор.РежимОтступСверху = 65;

        Монитор.логПоказатьСетку = true;
        Монитор.логПоказатьРамку = true;
        Монитор.логПоказатьДату = true;
        Монитор.логПоказатьВремя = true;
        Монитор.логПоказатьПлан = true;
        Монитор.логПоказатьРежим = true;
        Монитор.логПоказатьДиаграмму = true;
        Монитор.логПоказатьЛегенду = true;

        Планировщик.ВысотаЯчейки = 20;
        Планировщик.ШиринаЯчейки = 20;

        Звонки.логПредваритПередНачал = true;
        Звонки.логПредваритПередОконч = false;
        Звонки.ДлительностьОсновных = 30; // в секундах
        Звонки.ДлительностьПредварит = 15; // в секундах
        Звонки.ВремяДоНачала = 2; // в минутах
        Звонки.ВремяДоОкончания = 5; // в минутах

        Расписания.ИмяЗаписиВХранилище = "Расписания";
        Расписания.Режим = РЕЖИМ_СУТОЧНОЕ_РАСПИСАНИЕ;
        Расписания.ТекущийПлан = -1;
        Расписания.ТекущееРасписание = -1;

    }

    public void ИзМассиваБайт( byte[] Данные ) throws IOException {

        ByteArrayInputStream bin = new ByteArrayInputStream( Данные );
        DataInputStream din = new DataInputStream( bin );

        Основные.ИмяВХранилище = din.readUTF();
        Основные.МелодииПуть = din.readUTF();
        Основные.ФайлНастроек = din.readUTF();
        Основные.ФайлРасписаний = din.readUTF();
        Основные.ФайлЛога = din.readUTF();
        Основные.РазмерФайлаЛога = din.readInt();
        Основные.Кодировка = din.readInt();
        Основные.СуточнаяПоправка = din.readInt();
        Основные.Громкость = din.readInt();

        Монитор.ШиринаЭкрана = din.readInt();
        Монитор.ВысотаЭкрана = din.readInt();
        Монитор.ОтступXY = din.readInt();
        Монитор.ШагСеткиXY = din.readInt();
        Монитор.ПолныйЭкран = din.readBoolean();

        Монитор.ДиаграммаОтступСверху = din.readInt();
        Монитор.ДиаграммаТолщинаПолосы = din.readInt();
        Монитор.ДиаграммаДлинаИнтервала = din.readInt();

        Монитор.ПланОтступСверху = din.readInt();
        Монитор.РежимОтступСверху = din.readInt();
        Монитор.ВремяОтступСверху = din.readInt();

        Монитор.логПоказатьСетку = din.readBoolean();
        Монитор.логПоказатьРамку = din.readBoolean();
        Монитор.логПоказатьДату = din.readBoolean();
        Монитор.логПоказатьВремя = din.readBoolean();
        Монитор.логПоказатьПлан = din.readBoolean();
        Монитор.логПоказатьРежим = din.readBoolean();
        Монитор.логПоказатьДиаграмму = din.readBoolean();
        Монитор.логПоказатьЛегенду = din.readBoolean();

        Планировщик.ВысотаЯчейки = din.readInt();
        Планировщик.ШиринаЯчейки = din.readInt();

        Звонки.логПредваритПередНачал = din.readBoolean();
        Звонки.логПредваритПередОконч = din.readBoolean();
        Звонки.ДлительностьОсновных = din.readInt();
        Звонки.ДлительностьПредварит = din.readInt();
        Звонки.ВремяДоНачала = din.readInt();
        Звонки.ВремяДоОкончания = din.readInt();

        Расписания.ИмяЗаписиВХранилище = din.readUTF();
        Расписания.Режим = din.readInt();
        Расписания.ТекущееРасписание = din.readInt();
        Расписания.ТекущийПлан = din.readInt();

        din.close();
    }

    public byte[] ВМассивБайт() throws IOException {

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream( bout );

        dout.writeUTF( Основные.ИмяВХранилище );
        dout.writeUTF( Основные.МелодииПуть );
        dout.writeUTF( Основные.ФайлНастроек );
        dout.writeUTF( Основные.ФайлРасписаний );
        dout.writeUTF( Основные.ФайлЛога );
        dout.writeInt( Основные.РазмерФайлаЛога );
        dout.writeInt( Основные.Кодировка );
        dout.writeInt( Основные.СуточнаяПоправка );
        dout.writeInt( Основные.Громкость );

        dout.writeInt( Монитор.ШиринаЭкрана );
        dout.writeInt( Монитор.ВысотаЭкрана );
        dout.writeInt( Монитор.ОтступXY );
        dout.writeInt( Монитор.ШагСеткиXY );
        dout.writeBoolean( Монитор.ПолныйЭкран );

        dout.writeInt( Монитор.ДиаграммаОтступСверху );
        dout.writeInt( Монитор.ДиаграммаТолщинаПолосы );
        dout.writeInt( Монитор.ДиаграммаДлинаИнтервала );

        dout.writeInt( Монитор.ПланОтступСверху );
        dout.writeInt( Монитор.РежимОтступСверху );
        dout.writeInt( Монитор.ВремяОтступСверху );

        dout.writeBoolean( Монитор.логПоказатьСетку );
        dout.writeBoolean( Монитор.логПоказатьРамку );
        dout.writeBoolean( Монитор.логПоказатьДату );
        dout.writeBoolean( Монитор.логПоказатьВремя );
        dout.writeBoolean( Монитор.логПоказатьПлан );
        dout.writeBoolean( Монитор.логПоказатьРежим );
        dout.writeBoolean( Монитор.логПоказатьДиаграмму );
        dout.writeBoolean( Монитор.логПоказатьЛегенду );

        dout.writeInt( Планировщик.ВысотаЯчейки );
        dout.writeInt( Планировщик.ШиринаЯчейки );

        dout.writeBoolean( Звонки.логПредваритПередНачал );
        dout.writeBoolean( Звонки.логПредваритПередОконч );
        dout.writeInt( Звонки.ДлительностьОсновных );
        dout.writeInt( Звонки.ДлительностьПредварит );
        dout.writeInt( Звонки.ВремяДоНачала );
        dout.writeInt( Звонки.ВремяДоОкончания );

        dout.writeUTF( Расписания.ИмяЗаписиВХранилище );
        dout.writeInt( Расписания.Режим );
        dout.writeInt( Расписания.ТекущееРасписание );
        dout.writeInt( Расписания.ТекущийПлан );

        dout.close();

        return bout.toByteArray();
    }


    public String ToCP1251( String strIn ) {

        byte[] arOut = new byte[ strIn.length() ];

        StringBuffer sb = new StringBuffer();

        for ( int i = 0; i < strIn.length(); ++i ) {

            int ch = (int) strIn.charAt(i);

            switch ( ( char ) ch ) {

                case 'Ё':
                    ch -= m_arPhoneCharDiv[1];
                    break;

                case 'ё':
                    ch -= m_arPhoneCharDiv[2];
                    break;

                default:
                    if ( ch >= 192 ) ch -= m_arPhoneCharDiv[0];
            }

            arOut[i]=(byte)ch;

        }

        String str = new String( arOut );
        return  str;
    }


    public void ЭкспортНастроек( String ИмяФайла, int Кодировка ) {

        switch ( Кодировка ) {

            case Settings.КОДИРОВКА_UTF8:

                main.Логгер.info( "[Settings.java]: ЭкспортНастроек( \""
                        + ИмяФайла + "\", UTF-8 ); // Начало" );
                break;

            case Settings.КОДИРОВКА_CP1251:

                main.Логгер.info( "[Settings.java]: ЭкспортНастроек( \""
                        + ИмяФайла + "\", CP-1251 ); // Начало" );
                break;
        }

        String Текст = "";

        // Сохранение настроек
        Текст += "КОДИРОВКА_UTF8 = " + КОДИРОВКА_UTF8 + ";\n";
        Текст += "КОДИРОВКА_CP1251 = " + КОДИРОВКА_CP1251 + ";\n";
        Текст += "\n";

        Текст += "СУТОЧНОЕ_РАСПИСАНИЕ = " + РЕЖИМ_СУТОЧНОЕ_РАСПИСАНИЕ + ";\n";
        Текст += "ГОДОВОЙ_ПЛАН = " + РЕЖИМ_ГОДОВОЙ_ПЛАН + ";\n";
        Текст += "\n";

        Текст += "Настройки.Основные.МелодииПуть = \"";
        Текст += Основные.МелодииПуть + "\";\n";

        Текст += "Настройки.Основные.ФайлНастроек = \"";
        Текст += Основные.ФайлНастроек + "\";\n";

        Текст += "Настройки.Основные.ФайлРасписаний = \"";
        Текст += Основные.ФайлРасписаний + "\";\n";

        Текст += "Настройки.Основные.ФайлЛога = \"";
        Текст += Основные.ФайлЛога + "\";\n";

        Текст += "Настройки.Основные.РазмерФайлаЛога = ";
        Текст += Integer.toString( Основные.РазмерФайлаЛога ) + ";\n";

        Текст += "Настройки.Основные.Кодировка = ";

        switch ( Основные.Кодировка ) {

            case КОДИРОВКА_UTF8:
                Текст += "КОДИРОВКА_UTF8;\n";
                break;

            case КОДИРОВКА_CP1251:
                Текст += "КОДИРОВКА_CP1251;\n";
                break;
        }

        Текст += "Настройки.Основные.СуточнаяПоправка = ";
        Текст += Integer.toString( Основные.СуточнаяПоправка ) + ";\n";
        Текст += "\n";

        Текст += "Настройки.Основные.Громкость = ";
        Текст += Integer.toString( Основные.Громкость ) + ";\n";
        Текст += "\n";

        Текст += "Настройки.Монитор.ШиринаЭкрана = ";
        Текст += Integer.toString( Монитор.ШиринаЭкрана ) + ";\n";

        Текст += "Настройки.Монитор.ВысотаЭкрана = ";
        Текст += Integer.toString( Монитор.ВысотаЭкрана ) + ";\n";

        Текст += "Настройки.Монитор.ОтступXY = ";
        Текст += Integer.toString( Монитор.ОтступXY ) + ";\n";

        Текст += "Настройки.Монитор.ШагСеткиXY = ";
        Текст += Integer.toString( Монитор.ШагСеткиXY ) + ";\n";

        Текст += "Настройки.Монитор.логПолныйЭкран = ";
        Текст += ( Монитор.ПолныйЭкран ? "true" : "false" ) + ";\n";

        Текст += "Настройки.Монитор.ДиаграммаОтступСверху = ";
        Текст += Integer.toString( Монитор.ДиаграммаОтступСверху ) + ";\n";

        Текст += "Настройки.Монитор.ДиаграммаТолщинаПолосы = ";
        Текст += Integer.toString( Монитор.ДиаграммаТолщинаПолосы ) + ";\n";

        Текст += "Настройки.Монитор.ДиаграммаДлинаИнтервала = ";
        Текст += Integer.toString( Монитор.ДиаграммаДлинаИнтервала ) + ";\n"; // в минутах;

        Текст += "Настройки.Монитор.ПланОтступСверху = ";
        Текст += Integer.toString( Монитор.ПланОтступСверху ) + ";\n";

        Текст += "Настройки.Монитор.РежимОтступСверху = ";
        Текст += Integer.toString( Монитор.РежимОтступСверху ) + ";\n";

        Текст += "Настройки.Монитор.ВремяОтступСверху = ";
        Текст += Integer.toString( Монитор.ВремяОтступСверху ) + ";\n";

        Текст += "Настройки.Монитор.логПоказатьСетку = ";
        Текст += ( Монитор.логПоказатьСетку ? "true" : "false" ) + ";\n";

        Текст += "Настройки.Монитор.логПоказатьРамку = ";
        Текст += ( Монитор.логПоказатьРамку ? "true" : "false" ) + ";\n";

        Текст += "Настройки.Монитор.логПоказатьДату = ";
        Текст += ( Монитор.логПоказатьДату ? "true" : "false" ) + ";\n";

        Текст += "Настройки.Монитор.логПоказатьВремя = ";
        Текст += ( Монитор.логПоказатьВремя ? "true" : "false" ) + ";\n";

        Текст += "Настройки.Монитор.логПоказатьПлан = ";
        Текст += ( Монитор.логПоказатьПлан ? "true" : "false" ) + ";\n";

        Текст += "Настройки.Монитор.логПоказатьРежим = ";
        Текст += ( Монитор.логПоказатьРежим ? "true" : "false" ) + ";\n";

        Текст += "Настройки.Монитор.логПоказатьДиаграмму = ";
        Текст += ( Монитор.логПоказатьДиаграмму ? "true" : "false" ) + ";\n";

        Текст += "Настройки.Монитор.логПоказатьЛегенду = ";
        Текст += ( Монитор.логПоказатьЛегенду ? "true" : "false" ) + ";\n";
        Текст += "\n";

        Текст += "Настройки.Планировщик.ВысотаЯчейки = ";
        Текст += Integer.toString( Планировщик.ВысотаЯчейки ) + ";\n";

        Текст += "Настройки.Планировщик.ШиринаЯчейки = ";
        Текст += Integer.toString( Планировщик.ШиринаЯчейки ) + ";\n";
        Текст += "\n";

        Текст += "Настройки.Звонки.логПредваритПередНачал = ";
        Текст += ( Звонки.логПредваритПередНачал ? "true" : "false" ) + ";\n";

        Текст += "Настройки.Звонки.логПредваритПередОконч = ";
        Текст += ( Звонки.логПредваритПередОконч ? "true" : "false" ) + ";\n";

        Текст += "Настройки.Звонки.ДлительностьОсновных = ";
        Текст += Integer.toString( Звонки.ДлительностьОсновных ) + ";\n"; // в секундах

        Текст += "Настройки.Звонки.ДлительностьПредварит = ";
        Текст += Integer.toString( Звонки.ДлительностьПредварит ) + ";\n"; // в секундах

        Текст += "Настройки.Звонки.ВремяДоНачала = ";
        Текст += Integer.toString( Звонки.ВремяДоНачала ) + ";\n"; // в минутах

        Текст += "Настройки.Звонки.ВремяДоОкончания = ";
        Текст += Integer.toString( Звонки.ВремяДоОкончания ) + ";\n"; // в минутах
        Текст += "\n";

        Текст += "Настройки.Расписания.ИмяЗаписиВХранилище = \"";
        Текст += Расписания.ИмяЗаписиВХранилище + "\";\n";

        Текст += "Настройки.Расписания.Режим = ";

        switch ( Расписания.Режим ) {

            case РЕЖИМ_СУТОЧНОЕ_РАСПИСАНИЕ:
                Текст += "СУТОЧНОЕ_РАСПИСАНИЕ;\n";
                break;

            case РЕЖИМ_ГОДОВОЙ_ПЛАН:
                Текст += "ГОДОВОЙ_ПЛАН;\n";
                break;
        }

        Текст += "Настройки.Расписания.ТекущееРасписание = ";
        Текст += Integer.toString( Расписания.ТекущееРасписание ) + ";\n";

        Текст += "Настройки.Расписания.ТекущийПлан = ";
        Текст += Integer.toString( Расписания.ТекущийПлан ) + ";\n";

        switch ( Кодировка ) {

            case КОДИРОВКА_UTF8:

                СтрокаВФайлUTF8( ИмяФайла, Текст );
                main.Логгер.info( "[Settings.java]: ЭкспортНастроек( \""
                        + ИмяФайла + "\", UTF-8 ); // Конец" );
                break;
            case КОДИРОВКА_CP1251:

                СтрокаВФайлCP1251( ИмяФайла, Текст );
                main.Логгер.info( "[Settings.java]: ЭкспортНастроек( \""
                        + ИмяФайла + "\", CP-1251 ); // Конец" );
                break;
        }

    }


    public void ИмпортНастроек( String ИмяФайла, int Кодировка ) {

        String Текст = null;

        switch ( Кодировка ) {

            case КОДИРОВКА_UTF8:

                Текст = ФайлUTF8ВСтроку( ИмяФайла );
                main.Логгер.info( "[Settings.java]: ИмпортНастроек( \""
                        + ИмяФайла + "\", UTF-8 ); // Начало" );
                break;

            case КОДИРОВКА_CP1251:

                Текст = ФайлCP1251ВСтроку( ИмяФайла );
                main.Логгер.info( "[Settings.java]: ИмпортНастроек( \""
                        + ИмяФайла + "\", CP-1251 ); // Начало" );
                break;
        }

        Parser Разборщик = new Parser( Мидлет, this, Текст );

        try {

            do {

                Разборщик.ПолучитьЭлемент();
                Разборщик.СложениеВычитание( false );

            } while ( Разборщик.Свойства.ТекущийЭлемент != Parser.ЭЛЕМЕНТ_КОНЕЦ );
//            } while ( ( Разборщик.Свойства.ТекущийЭлемент == Parser.ЭЛЕМЕНТ_ВЫВОД )
//                    || ( Разборщик.Свойства.ТекущийЭлемент != Parser.ЭЛЕМЕНТ_КОНЕЦ ) );
        } catch ( Exception Исключение ) {

            main.Логгер.error( "[Settings.java]: Ошибка разборщика. " + Исключение.getMessage() );
            return;
        }

        // Инициализация настроек
        Parser.ОбразРезультата врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Основные.МелодииПуть" );
        if ( врм != null )
            Основные.МелодииПуть = ( String) врм.Значение.elementAt(0);

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Основные.ФайлНастроек" );
        if ( врм != null )
            Основные.ФайлНастроек = ( String) врм.Значение.elementAt(0);

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Основные.ФайлРасписаний" );
        if ( врм != null )
            Основные.ФайлРасписаний = ( String) врм.Значение.elementAt(0);

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Основные.ФайлЛога" );
        if ( врм != null )
            Основные.ФайлЛога = ( String) врм.Значение.elementAt(0);

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Основные.РазмерФайлаЛога" );
        if ( врм != null )
            Основные.РазмерФайлаЛога = ( ( Integer) врм.Значение.elementAt(0) ).intValue();

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Основные.Кодировка" );
        if ( врм != null )
            Основные.Кодировка = ( ( Integer) врм.Значение.elementAt(0) ).intValue();

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Основные.СуточнаяПоправка" );
        if ( врм != null )
            Основные.СуточнаяПоправка = ( ( Integer) врм.Значение.elementAt(0) ).intValue();

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Основные.Громкость" );
        if ( врм != null )
            Основные.Громкость = ( ( Integer) врм.Значение.elementAt(0) ).intValue();

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Монитор.ШиринаЭкрана" );
        if ( врм != null )
            Монитор.ШиринаЭкрана = ( ( Integer) врм.Значение.elementAt(0) ).intValue();

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Монитор.ВысотаЭкрана" );
        if ( врм != null )
            Монитор.ВысотаЭкрана = ( ( Integer) врм.Значение.elementAt(0) ).intValue();

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Монитор.ОтступXY" );
        if ( врм != null )
            Монитор.ОтступXY = ( ( Integer) врм.Значение.elementAt(0) ).intValue();

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Монитор.ШагСеткиXY" );
        if ( врм != null )
            Монитор.ШагСеткиXY = ( ( Integer) врм.Значение.elementAt(0) ).intValue();

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Монитор.логПолныйЭкран" );
        if ( врм != null )
            Монитор.ПолныйЭкран = ( ( Boolean) врм.Значение.elementAt(0) ).booleanValue();

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Монитор.ДиаграммаОтступСверху" );
        if ( врм != null )
            Монитор.ДиаграммаОтступСверху = ( ( Integer) врм.Значение.elementAt(0) ).intValue();

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Монитор.ДиаграммаТолщинаПолосы" );
        if ( врм != null )
            Монитор.ДиаграммаТолщинаПолосы = ( ( Integer) врм.Значение.elementAt(0) ).intValue();

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Монитор.ДиаграммаДлинаИнтервала" );
        if ( врм != null )
            Монитор.ДиаграммаДлинаИнтервала = ( ( Integer) врм.Значение.elementAt(0) ).intValue();

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Монитор.ПланОтступСверху" );
        if ( врм != null )
            Монитор.ПланОтступСверху = ( ( Integer) врм.Значение.elementAt(0) ).intValue();

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Монитор.РежимОтступСверху" );
        if ( врм != null )
            Монитор.РежимОтступСверху = ( ( Integer) врм.Значение.elementAt(0) ).intValue();

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Монитор.ВремяОтступСверху" );
        if ( врм != null )
            Монитор.ВремяОтступСверху = ( ( Integer) врм.Значение.elementAt(0) ).intValue();

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Монитор.логПоказатьСетку" );
        if ( врм != null )
            Монитор.логПоказатьСетку = ( ( Boolean) врм.Значение.elementAt(0) ).booleanValue();

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Монитор.логПоказатьРамку" );
        if ( врм != null )
            Монитор.логПоказатьРамку = ( ( Boolean) врм.Значение.elementAt(0) ).booleanValue();

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Монитор.логПоказатьДату" );
        if ( врм != null )
            Монитор.логПоказатьДату = ( ( Boolean) врм.Значение.elementAt(0) ).booleanValue();

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Монитор.логПоказатьВремя" );
        if ( врм != null )
            Монитор.логПоказатьВремя = ( ( Boolean) врм.Значение.elementAt(0) ).booleanValue();

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Монитор.логПоказатьПлан" );
        if ( врм != null )
            Монитор.логПоказатьПлан = ( ( Boolean) врм.Значение.elementAt(0) ).booleanValue();

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Монитор.логПоказатьРежим" );
        if ( врм != null )
            Монитор.логПоказатьРежим = ( ( Boolean) врм.Значение.elementAt(0) ).booleanValue();

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Монитор.логПоказатьДиаграмму" );
        if ( врм != null )
            Монитор.логПоказатьДиаграмму = ( ( Boolean) врм.Значение.elementAt(0) ).booleanValue();

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Монитор.логПоказатьЛегенду" );
        if ( врм != null )
            Монитор.логПоказатьЛегенду = ( ( Boolean) врм.Значение.elementAt(0) ).booleanValue();

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Планировщик.ВысотаЯчейки" );
        if ( врм != null )
            Планировщик.ВысотаЯчейки = ( ( Integer) врм.Значение.elementAt(0) ).intValue();

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Планировщик.ШиринаЯчейки" );
        if ( врм != null )
            Планировщик.ШиринаЯчейки = ( ( Integer) врм.Значение.elementAt(0) ).intValue();

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Звонки.логПредваритПередНачал" );
        if ( врм != null )
            Звонки.логПредваритПередНачал = ( ( Boolean) врм.Значение.elementAt(0) ).booleanValue();

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Звонки.логПредваритПередОконч" );
        if ( врм != null )
            Звонки.логПредваритПередОконч = ( ( Boolean) врм.Значение.elementAt(0) ).booleanValue();

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Звонки.ДлительностьОсновных" );
        if ( врм != null )
            Звонки.ДлительностьОсновных = ( ( Integer) врм.Значение.elementAt(0) ).intValue();

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Звонки.ДлительностьПредварит" );
        if ( врм != null )
            Звонки.ДлительностьПредварит = ( ( Integer) врм.Значение.elementAt(0) ).intValue();

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Звонки.ВремяДоНачала" );
        if ( врм != null )
            Звонки.ВремяДоНачала = ( ( Integer) врм.Значение.elementAt(0) ).intValue();

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Звонки.ВремяДоОкончания" );
        if ( врм != null )
            Звонки.ВремяДоОкончания = ( ( Integer) врм.Значение.elementAt(0) ).intValue();

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Расписания.ИмяЗаписиВХранилище" );
        if ( врм != null )
            Расписания.ИмяЗаписиВХранилище = ( String) врм.Значение.elementAt(0);

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Расписания.Режим" );
        if ( врм != null )
            Расписания.Режим = ( ( Integer) врм.Значение.elementAt(0) ).intValue();

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Расписания.ТекущееРасписание" );
        if ( врм != null )
            Расписания.ТекущееРасписание = ( ( Integer) врм.Значение.elementAt(0) ).intValue();

        врм = Разборщик.Переменные.ЗначениеЭлемента( "Настройки.Расписания.ТекущийПлан" );
        if ( врм != null )
            Расписания.ТекущийПлан = ( ( Integer) врм.Значение.elementAt(0) ).intValue();

        switch ( Кодировка ) {

            case КОДИРОВКА_UTF8:

                СтрокаВФайлUTF8( ИмяФайла, Текст );
                main.Логгер.info( "[Settings.java]: ИмпортНастроек( \""
                        + ИмяФайла + "\", UTF-8 ); // Конец" );
                break;

            case КОДИРОВКА_CP1251:

                СтрокаВФайлCP1251( ИмяФайла, Текст );
                main.Логгер.info( "[Settings.java]: ИмпортНастроек( \""
                        + ИмяФайла + "\", CP-1251 ); // Конец" );
                break;
        }

    }


    public void СтрокаВФайлCP1251( String ПутькФайлу, String Текст ) {

        Текст = ToCP1251( Текст );

        try {

            FileConnection fc = ( FileConnection ) Connector.open( ПутькФайлу );
            if (fc.exists()) fc.delete();
            fc.create();

            ByteArrayOutputStream boutput = new ByteArrayOutputStream(); // сюда будем писать УТФ байты
            DataOutputStream doutput = new DataOutputStream(boutput); // создаем писатель Java-UTF
            //doutput.writeUTF( Текст ); // пишем байты в байтовый массив
            doutput.write( Текст.getBytes() );
            DataOutputStream dosf = fc.openDataOutputStream();
            dosf.write( boutput.toByteArray(), 2, boutput.size() - 2 );

            dosf.flush();
            dosf.close();
            fc.close();

        } catch ( IOException ИсключениеВводаВывода ) {

            main.Логгер.error( "[Settings.java]: Ошибка ввода/вывода: "
                    + "'" + ПутькФайлу + "'. "
                    + ИсключениеВводаВывода.getMessage() );
        }

    }


    public String ФайлCP1251ВСтроку( String ПутькФайлу ) {

        StringBuffer strBuff = new StringBuffer();

        try {

            FileConnection fc = ( FileConnection ) Connector.open( ПутькФайлу );

            if ( !fc.exists() ) {

                throw new IOException ( "File does not exists" );
            }

            InputStream is = fc.openInputStream();

            int ch = 0;

            try {

                while ( (ch = is.read()) != -1 ) {

                    strBuff.append( ( char ) ( ( ch >= 0xc0 && ch <= 0xFF ) ? ( ch + 0x350 ) : ch ) );
                }

                is.close();
                fc.close();

            } catch ( IOException ИсключениеВводаВывода ) {

                main.Логгер.error( "[Settings.java]: Ошибка ввода/вывода: "
                        + "'" + ПутькФайлу + "'. "
                        + ИсключениеВводаВывода.getMessage() );
            }

        } catch ( Exception Исключение ) {

            main.Логгер.error( "[Settings.java]: Неизвестное исключение. "
                    + Исключение.getMessage() );
        }

        return strBuff.toString();
    }


    public void СтрокаВФайлUTF8( String ПутькФайлу, String Текст ) {

        try {

            FileConnection fc = ( FileConnection ) Connector.open( ПутькФайлу );
            if ( fc.exists() ) fc.delete();
            fc.create();

            ByteArrayOutputStream boutput = new ByteArrayOutputStream(); // сюда будем писать УТФ байты
            DataOutputStream doutput = new DataOutputStream(boutput); // создаем писатель Java-UTF

            doutput.writeUTF( Текст ); // пишем байты в байтовый массив
            DataOutputStream dosf = fc.openDataOutputStream();
            dosf.write( boutput.toByteArray(), 2, boutput.size() - 2 );

            dosf.flush();
            dosf.close();
            fc.close();

        } catch ( IOException ИсключениеВводаВывода ) {

            main.Логгер.error( "[Settings.java]: Ошибка ввода/вывода: "
                    + "'" + ПутькФайлу + "'. "
                    + ИсключениеВводаВывода.getMessage() );
        }

    }


    public String ФайлUTF8ВСтроку( String ПутькФайлу ) {

       StringBuffer sb = new StringBuffer( 256 );

       try {

           int[] surrogatePair = new int[2];
            FileConnection fc = ( FileConnection ) Connector.open ( ПутькФайлу );

            if ( !fc.exists () ) {

                throw new IOException ( "Файл не существует." );
            }

           InputStream is = fc.openInputStream();

           int val = 0;
           int unicharCount = 0;
           while ( ( val = readNextCharFromStreamUTF8(is)) != -1 ) {

               unicharCount++;

               if ( val <= 0xFFFF ) {

                   // if first value is the Byte Order Mark (BOM), do not add
                   if (! ( unicharCount == 1 && val == 0xFEFF ) ) {

                       sb.append( ( char ) val );
                   }

               } else {

                   supplementCodePointToSurrogatePair( val, surrogatePair );
                   sb.append((char)surrogatePair[0]);
                   sb.append((char)surrogatePair[1]);

               }

           }

           is.close();
           fc.close();

       } catch ( IOException ИсключениеВводаВывода ) {

            main.Логгер.error( "[Settings.java]: Ошибка ввода/вывода: "
                    + "'" + ПутькФайлу + "'. "
                    + ИсключениеВводаВывода.getMessage() );
        }

       return new String(sb);
   }


   private int readNextCharFromStreamUTF8( InputStream is ) {

       int c = -1;
       if ( is == null ) return c;
       boolean complete = false;

       try {

           int byteVal;
           int expecting = 0;
           int composedVal = 0;

           while ( !complete && ( byteVal = is.read() ) != -1 ) {

               if ( expecting > 0 && ( byteVal & 0xC0 ) == 0x80 ) {  /* 10xxxxxx */

                   expecting--;
                   composedVal = composedVal | ( ( byteVal & 0x3F ) << ( expecting * 6 ) );

                   if ( expecting == 0 ) {

                       c = composedVal;
                       complete = true;
                       //System.out.println("appending: U+" + Integer.toHexString(composedVal) );

                   }

               } else {

                   composedVal = 0;
                   expecting = 0;

                   if ( ( byteVal & 0x80 ) == 0 ) {    /* 0xxxxxxx */

                       // one byte character, no extending byte expected
                       c = byteVal;
                       complete = true;
                       //System.out.println("appending: U+" + Integer.toHexString(byteVal) );

                   } else if ( ( byteVal & 0xE0 ) == 0xC0 ) {   /* 110xxxxx */

                       expecting = 1;  // expecting 1 extending byte
                       composedVal = ( ( byteVal & 0x1F ) << 6 );

                   } else if ( ( byteVal & 0xF0 ) == 0xE0 ) {   /* 1110xxxx */

                       expecting = 2;  // expecting 2 extending bytes
                       composedVal = ( ( byteVal & 0x0F ) << 12 );

                   } else if ( ( byteVal & 0xF8 ) == 0xF0 ) {   /* 11110xxx */

                       expecting = 3;  // expecting 3 extending bytes
                       composedVal = ( ( byteVal & 0x07 ) << 18 );

                   } else {
                       // non conformant utf-8, ignore or catch error
                   }

               }

           }

       } catch ( Exception Ошибка ) {

            main.Логгер.error( "[Settings.java]: Ошибка преобразования " + Ошибка.getMessage() );
       }

       return c;
   }


   private final static void supplementCodePointToSurrogatePair( int codePoint, int[] surrogatePair ) {

       int high4 = ( ( codePoint >> 16 ) & 0x1F ) - 1;
       int mid6 = ( ( codePoint >> 10 ) & 0x3F );
       int low10 = codePoint & 0x3FF;

       surrogatePair[0] = (0xD800 | (high4 << 6) | (mid6));
       surrogatePair[1] = (0xDC00 | (low10));
   }


    public String ФайлUTF8ИзРесурсовВСтроку( String s ) throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];

        InputStream istream = getClass().getResourceAsStream(s);
        boolean done = false;

        while( !done ) {

            int count = istream.read( buffer );
            baos.write( buffer, 0, count );
            done = ( count != -1 );

        }

        byte[] content = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream( content );
        DataInputStream isr = new DataInputStream( bais );

        String s1 = "";
        for ( int z = isr.available() / 2; z > 0; z-- ) s1 += isr.readChar();

        return s1.substring(1);
    }


    public String ФайлCP1251ИзРесурсовВСтроку( String path ) {

        DataInputStream dis = new DataInputStream( getClass().getResourceAsStream( path ) );
        StringBuffer strBuff = new StringBuffer();
        int ch = 0;

        try {

            while ( ( ch = dis.read() ) != -1 ) {

                strBuff.append( ( char ) ( ( ch >= 0xc0 && ch <= 0xFF ) ? ( ch + 0x350 ) : ch ) );
            }

            dis.close();

        } catch ( Exception Ошибка ) {

            main.Логгер.error( "[Settings.java]: Ошибка чтения из ресурсов " + Ошибка.getMessage() );
        }

        return strBuff.toString();
    }

}

