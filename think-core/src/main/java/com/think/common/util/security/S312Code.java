package com.think.common.util.security;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2023/2/7 16:15
 * @description :
 */
public class S312Code {
    final static char[] dic = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z',
            'α', 'β', 'γ', 'δ', 'ε', 'ζ', 'η', 'θ', 'ι', 'κ',
            'λ', 'μ', 'ν', 'ξ', 'ο', 'π', 'ρ', 'ς', 'σ', 'τ',
            'υ', 'φ', 'ψ', 'ω',
            '①','②','③','④','⑤','⑥','⑦','⑧','⑨','⑩',
            '⑪','⑫','⑬','⑭','⑮','⑯','⑰','⑱','⑲','⑳',
            'Ⓐ','Ⓑ','Ⓒ','Ⓓ','Ⓔ','Ⓕ','Ⓖ','Ⓗ','Ⓘ','Ⓙ',
            'Ⓚ','Ⓛ','Ⓜ','Ⓝ','Ⓞ','Ⓟ','Ⓠ','Ⓡ','Ⓢ','Ⓣ',
            'Ⓤ','Ⓥ','Ⓦ','Ⓧ','Ⓨ','Ⓩ',
            '♔','♕','♖','♗','♘','♙','♚','♛','♜','♝','♞','♟',
            '♠','♡','♢','♣','♤','♥','♦','♧','♨','♩','♪','♫','♬',
            '♭','♮','♯','⚐','⚑','⚒','⚓','⚔','⚕','⚖','⚗','⚘','⚙',
            '⚚','⚛','⚜','⚝','⚞','⚟','⚠','⚡','⚢','⚣','⚤','⚥','⚦',
            '⚧','⚨','⚩',
            'Ⲉ','ⲉ','Ⲋ','ⲋ','Ⲯ','ⲯ','Ⲱ','ⲱ','Ⲳ','ⲳ','Ⲵ','ⲵ','Ⲷ','ⲷ','Ⲹ','ⲹ',
            '⨶','⨷','⨸','⨹','⨺','⨻','⨼','⨽','⨾','⨿',
            '⩀','⩁','⩂','⩃','⩄','⩅','⩆','⩇','⩈','⩉','⩊','⩋',
            '⩌','⩍','⩎','⩏','⩐','⩑','⩒','⩓','⩔','⩕','⩖','⩗',
            '⩘','⩙','⩚','⩛','⩜','⩝','⩞','⩟','⩠','⩡','⩢','⩣',
            '⩤','⩥','⩦','⩧','⩨','⩩','⩪','⩫','⩬','⩭','⩮','⩯',
            '⩰','⩱','⩲','⩳',
            '⬟','⬠','⬡','⬢','⬣','⬤','⬥','⬦','⬧','⬨',
            '⬪','⬫','⬬','⬭','⬮','⬯','⮈','⮉','⮊','⮋',
            '⮌','⮍','⮎','⮏','⮐','⮑','⮒','⮓','⮔',
            'Ⰲ','Ⰳ','Ⰴ','Ⰵ','Ⰶ','Ⰷ','Ⰸ','Ⰹ','Ⰺ','Ⰻ','Ⰼ','Ⰽ','Ⰾ','Ⰿ',
            'Ⱀ','Ⱁ','Ⱂ','Ⱃ','Ⱄ','Ⱅ','Ⱆ','Ⱇ','Ⱈ','Ⱉ','Ⱊ','Ⱋ','Ⱌ','Ⱍ','Ⱎ',
            'Ⱏ','Ⱐ','Ⱑ','Ⱒ','Ⱓ','Ⱔ','Ⱕ','Ⱖ','Ⱗ','Ⱘ','Ⱙ','Ⱚ','Ⱛ','Ⱜ','Ⱝ','Ⱞ'

    };
    final static int base = dic.length;

    private static final int indexOf(char c){
        for (int i = 0 ; i < dic.length;i++){
            if(dic[i] == c){
                return i;
            }
        }
        return -1;
    }


    /**
     * 编码
     * @param source
     * @return
     */
    public static final String encodeString(String source){
        StringBuilder sb =new StringBuilder();
        for (char c : source.toCharArray()) {
            sb.append( encodeChar(c,2));
        }

        return sb.toString();
    }

    /**
     * 解码
     * @param encodeString
     * @return
     */
    public static final String decodeString(String encodeString){
        StringBuilder tempStr = new StringBuilder();
        StringBuilder decode =new StringBuilder();
        char[] chars = encodeString.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            tempStr.append(chars[i]);
            if(tempStr.length() ==2){
                decode.append(decodeChar(tempStr.toString()));
                tempStr = new StringBuilder();
            }
        }
        return decode.toString();
    }


    /**
     * 单字符编码
     * @param c
     * @param requiredLen
     * @return
     */
    private static final String encodeChar(char c , int requiredLen){
        int n = c;
        StringBuilder s = new StringBuilder();
        while (n >= base){
            int t = n % base;
            s.insert(0,dic[t]);
            n = n /base;
        }
        s.insert(0,dic[n]);
        while (s.length()<requiredLen){
            s.insert(0,dic[0]);
        }
        return s.toString();
    }

    /**
     * 解码
     * @param code
     * @return
     */
    private static final char decodeChar(String code){
        char[] chars = code.toCharArray();
        int source = 0;
        int pos =0;
        for (int i = chars.length - 1; i >= 0; i--) {
            int index = indexOf(chars[i]);
            int tpos = pos;
            while (tpos>0){
                tpos--;
                index*=base;
            }
            source+= index;
            pos++;
        }
        return (char) source;
    }

    public static String parallelEncode(String str ){
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {

        }
        return null;

    }






    public static void main(String[] args) {
        String source ="其也可以指SONY公司旗下的相机品牌α。α（alpha），是Sony公司的数位单眼相机（DSLR）品牌。Sony自1997年第一台CyberShot F1问世以来至今，配合自身在硬体领域上的优异技术与卓越的CCD感光元件，另外搭载德国卡尔·蔡司（Carl Zeiss）镜头，在全球消费性数位相机市场取得占有率第二，并和尼康、佳能、富士、柯达并列数位相机五大品牌。Sony拥有优异的影像感光元件技术，却缺乏镜头技术，虽然与德国光学名厂卡尔·蔡司（Carl Zeiss）合作制造高阶消费性数位相机，但无法与高阶级的数位单眼相机（DSLR）抗衡。为此，2005年7月19日，Sony发表与柯尼卡美能达（Konica Minolta）合作加入数位单眼相机市场，以因应消费型数位相机市场的饱和。2006年1月19日，Sony发布自2006年4月1日起全面接管柯尼卡美能达影像事业部门。2006年4月20日，Sony发表数位单眼相机品牌α(alpha)，并在该年6月7日正式发表Sony首部DSLR——α100。\n" +
                "☆「Alpha」常用作形容词，以显示某件事物中最重要或最初的，例如软体工程中的Alpha版本或生物学中的Alpha男子。\n" +
                "2.基督教派中 ，第1个希腊字母α 代表“开始”， 希腊最后一个字母Ω代表“结束”，特指上帝创造万物，有开始，有结束。例如：我是Alpha、我是Omega、我是首先的、我是末后的、我是初、我是终。（圣经启示录 22:13）读音：欧米伽 拼音：omiga。\n" +
                "3.在现代欧洲的文化里，也有用α代表“领袖”，优秀（的人），而Ω表示“被领袖”，比优秀差（的人）。如动画片《丛林有情狼》，就像是用A和Z分别代表第一名和最后一名。\n" +
                "4.在数学中，是系数、角度、一元二次方程的一个根和第二费根鲍姆常数的意思。\n" +
                "附：α与a相似，但不为同一字符，且用来表示角的名称还有β，γ等欧洲，全称“欧罗巴洲”（Europe），名字源于希腊神话的人物“欧罗巴”（希腊语：Ευρώπης），欧洲科技具备优势和影响力： [19]  欧洲位于东半球的西北部，北临北冰洋，西濒大西洋，南滨大西洋的属海地中海和黑海。大陆东至极地乌拉尔山脉（66°10′E，67°46′N），南至马罗基角（5°36′W，36°N），西至罗卡角（9°31′W，38°47′N），北至诺尔辰角（27°42′E，71°08′N）。 [1] \n" +
                "欧洲面积居世界第六，人口密度70人/km²，是世界人口第三的洲，仅次于亚洲和非洲，99%以上人口属白色人种，比较单一。欧洲是人类生活水平较高、环境以及人类发展指数较高及适宜居住的大洲之一。 [1] \n" +
                "欧洲东以乌拉尔山脉、乌拉尔河，东南以里海、大高加索山脉和黑海与亚洲为界，西隔大西洋、格陵兰海、丹麦海峡与北美洲相望，北接北冰洋，南隔地中海与非洲相望（分界线为：直布罗陀海峡）。\n" +
                "欧洲最北端是挪威的诺尔辰角，最南端是西班牙的马罗基角，最西端是葡萄牙的罗卡角。欧洲是世界上第二小的洲、大陆，仅比大洋洲大一些，其与亚洲合称为亚欧大陆，而与亚洲、非洲合称为亚欧非大陆。\n" +
                "因为文化、经济、政治等原因，欧洲的边界总是不一样的，所以就有了多个‘欧洲’的概念。 [";
        System.out.println(source.length());
        System.out.println(encodeString(source));
        System.out.println(encodeString(source).length());
        System.out.println(decodeString(encodeString(source)));

    }



}
