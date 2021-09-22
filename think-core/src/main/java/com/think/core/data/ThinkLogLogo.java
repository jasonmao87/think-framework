package com.think.core.data;

/**
 * @Date :2021/9/18
 * @Name :ThinkLogLogo
 * @Description : 请输入
 */
public class ThinkLogLogo {

    public static final String logLogo = "" +
            "         THINK-DID-FRAMEWORKS#                                                           \n" +
            "      CLOUD-THINK-FARM#THINK-DID-                 FRAMEWORKS#CLOUD-THINK-FARM#THINK-DID-F\n" +
            "    RAMEWORKS#CLOUD-THINK-FARM#THINK            -DID-FRAMEWORKS#CLOUD-THINK-FARM#THINK-DI\n" +
            "   D-FRAMEWORKS#CLOUD-THINK-FARM#THINK          -DID-FRAMEWORKS#CLOUD-THINK-FARM#THINK-DI\n" +
            "  D-FRAMEWORKS#CLOUD-THINK-FARM#THINK-DI        D-FRAMEWORKS#CLOUD-THINK-FARM#THINK-DID-F\n" +
            "  RAMEWORKS#CLOUD-THINK-FARM#THINK-DID-FRAM     EWORKS#CLOUD-THINK-FARM#THINK-DID-FRAMEWO\n" +
            "  RKS#CLOUD-THINK-FARM#THINK-DID-FR  AMEWORK    S#CLOUD-THINK-FARM#THINK-DID-FRAMEWORKS#C\n" +
            "  LOUD-THINK-FARM#THINK-DID-FRAMEWO   RKS#CL    OUD-THINK-FARM#THINK-DID-FRAMEWORKS#CLOUD\n" +
            "  -THINK   -FARM#   THINK-   DID-FR    AMEWO    RKS#CLOUD-THINK-FARM#THINK-DID-FRAMEWORKS\n" +
            "  #CLOUD   -THINK   -FARM#   THINK-             DID-FRAMEWORKS#CLOUD-THINK-FARM#THINK-DID\n" +
            "  -FRAME   WORKS#   CLOUD-   THINK-             FARM#THINK-DID-FRAMEWORKS#CLOUD-THINK-FAR\n" +
            "  M#THIN   K-DID-   FRAMEW   ORKS#C             LOUD-THINK-FARM#THINK-DID-FRAMEWORKS#CLOU\n" +
            "  D-THIN   K-FARM   #THINK   -DID-F             RAMEWORKS#CLOUD-THINK-FARM#THINK-DID-FRAM\n" +
            "  EWORKS   #CLOUD   -THINK   -FARM#              THINK-DID-FRAMEWORKS#CLOUD-THINK-FARM#TH\n" +
            "  INK-DI   D-FRAM   EWORKS   #CLOUD               -THINK-FARM#THINK-DID-FRAMEWORKS#CLOU  \n" +
            "   D-THI    NK-F     ARM#T    HINK                                            -DID-FRA   \n" +
            "                                                                              MEWORK     \n" +
            "            S                                                                 #CLO       \n" +
            "           UD                                                                 -TH        \n" +
            "          INK                                                                 -          \n" +
            "       FARM#T                                                                            \n" +
            "     HINK-DID                                            -FRA    MEWOR     KS#C    LOUD- \n" +
            "    THINK-FARM#THINK-DID-FRAMEWORKS#CLOUD               -THINK   -FARM#   THINK-   DID-FR\n" +
            "  AMEWORKS#CLOUD-THINK-FARM#THINK-DID-FRAM              EWORKS   #CLOUD   -THINK   -FARM#\n" +
            "  THINK-DID-FRAMEWORKS#CLOUD-THINK-FARM#THI             NK-DID   -FRAME   WORKS#   CLOUD-\n" +
            "  THINK-FARM#THINK-DID-FRAMEWORKS#CLOUD-THI             NK-FAR   M#THIN   K-DID-   FRAMEW\n" +
            "  ORKS#CLOUD-THINK-FARM#THINK-DID-FRAMEWORK             S#CLOU   D-THIN   K-FARM   #THINK\n" +
            "  -DID-FRAMEWORKS#CLOUD-THINK-FARM#THINK-DI             D-FRAM   EWORKS   #CLOUD   -THINK\n" +
            "  -FARM#THINK-DID-FRAMEWORKS#CLOUD-THINK-FA    RM#TH    INK-DI   D-FRAM   EWORKS   #CLOUD\n" +
            "  -THINK-FARM#THINK-DID-FRAMEWORKS#CLOUD-TH    INK-FA   RM#THINK-DID-FRAMEWORKS#CLOUD-THI\n" +
            "  NK-FARM#THINK-DID-FRAMEWORKS#CLOUD-THINK-    FARM#TH  INK-DID-FRAMEWORKS#CLOUD-THINK-FA\n" +
            "  RM#THINK-DID-FRAMEWORKS#CLOUD-THINK-FARM#     THINK-DID-FRAMEWORKS#CLOUD-THINK-FARM#THI\n" +
            "  NK-DID-FRAMEWORKS#CLOUD-THINK-FARM#THINK-        DID-FRAMEWORKS#CLOUD-THINK-FARM#THINK-\n" +
            "  DID-FRAMEWORKS#CLOUD-THINK-FARM#THINK-DID          -FRAMEWORKS#CLOUD-THINK-FARM#THINK- \n" +
            "  DID-FRAMEWORKS#CLOUD-THINK-FARM#THINK-DID            -FRAMEWORKS#CLOUD-THINK-FARM#THI  \n" +
            "  NK-DID-FRAMEWORKS#CLOUD-THINK-FARM#THIN                 K-DID-FRAMEWORKS#CLOUD-THIN    \n" +
            "                                                             K-FARM#THINK-DID-FRAME      \n" ;


    public static final String logoStyleA(){
        return logLogo;
    }

    public static final String logStyleB(){
        char[] dic =  "THINK-FRAMEWORKS@WWW_THINKDID_COM#JASON_MAO@87#CLOUD-SMART-HOSPITAL-MANAGEMENT_".toCharArray();
        StringBuilder style = new StringBuilder();
        int index = 0 ;
        for (char x : logLogo.toCharArray()) {
            if(x == '\n'){
                style.append(x);
            }else{
                char current = dic[index>=dic.length?index%dic.length:index];
                if(x !=' '){
                    style.append(current);
                }else{
                    style.append(x);
                }
                index ++ ;

            }
        }
        return style.toString();
    }

}
