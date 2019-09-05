package com.bangmart.bmtvendingmachinedemo.androidboard.constant;

import com.bangmart.bmtvendingmachinedemo.constant.ComId;

/**
 * Created by zhoujq on 2019/1/24.
 * Android板常量
 */
public interface AndroidBoardConstant {

    interface BANGMART{
        interface SerialPortInfoConstant {

            interface SerialPortOne{
                int COM_ID=ComId.ID_ONE;
                String SERIAL_PORT_DEVICE_NAME="/dev/ttyS0";
            }

            interface SerialPortTwo{
                int COM_ID=ComId.ID_TWO;
                String SERIAL_PORT_DEVICE_NAME="/dev/ttyS1";
            }

            interface SerialPortThree{
                int COM_ID=ComId.ID_THREE;
                String SERIAL_PORT_DEVICE_NAME="/dev/ttyS3";
            }

            interface SerialPortFour{
                int COM_ID=ComId.ID_FOUR;
                String SERIAL_PORT_DEVICE_NAME="/dev/ttyS4";
            }
        }
    }

    interface FSK{
        interface SerialPortInfoConstant {

            /**
             * Com口:可以用1和2,默认1   3456 预留
             */

            interface SerialPortOne{
                int COM_ID= ComId.ID_ONE;
                String SERIAL_PORT_DEVICE_NAME="/dev/ttymxc1";
            }

            interface SerialPortTwo{
                int COM_ID= ComId.ID_TWO;
                String SERIAL_PORT_DEVICE_NAME="/dev/ttymxc2";
            }

    /*interface SerialPortThree{
        int COM_ID= ComId.ID_THREE;
        String SERIAL_PORT_DEVICE_NAME="/dev/ttyUSB0";
    }

    interface SerialPortFour{
        int COM_ID=ComId.ID_FOUR;
        String SERIAL_PORT_DEVICE_NAME="/dev/ttyUSB3";
    }

    interface SerialPortFive{
        int COM_ID=ComId.ID_FIVE;
        String SERIAL_PORT_DEVICE_NAME="/dev/ttyUSB1";
    }

    interface SerialPortSix{
        int COM_ID=ComId.ID_SIX;
        String SERIAL_PORT_DEVICE_NAME="/dev/ttyUSB2";
    }*/
        }

    }
    /**
     * 彤兴Android板
     * http://www.ostar-display.cn
     */
      interface OSTAR{
        interface SerialPortInfoConstant {

            interface SerialPortOne{
                int COM_ID=ComId.ID_ONE;
                String SERIAL_PORT_DEVICE_NAME="/dev/ttyS0";
            }

            //其它的待添加
        }
    }

    /**
     * 众云世纪 //视美讯RK3288
     *
     * http://www.zysj-sz.com/
     */
    interface ZHONG_YUN_SHI_JI{
        interface SerialPortInfoConstant {

           /* interface SerialPortOne{
                int COM_ID=ComId.ID_ONE;
                //无此串口
                String SERIAL_PORT_DEVICE_NAME="/dev/ttyS0";
            }*/

            interface SerialPortTwo{
                int COM_ID= ComId.ID_TWO;
                //有此串口
                String SERIAL_PORT_DEVICE_NAME="/dev/ttyS1";
            }

            interface SerialPortThree{
                int COM_ID=ComId.ID_THREE;
                //有此串口
                String SERIAL_PORT_DEVICE_NAME="/dev/ttyS3";
            }

            interface SerialPortFour{
                int COM_ID=ComId.ID_FOUR;
                //有此串口
                String SERIAL_PORT_DEVICE_NAME="/dev/ttyS4";
            }
        }
    }

    /**
     * 云印-昱显android板
     */
    interface YUN_YIN_AND_YU_XIAN{
        interface SerialPortInfoConstant {

            interface SerialPortFour{
                int COM_ID=ComId.ID_FOUR;
                String SERIAL_PORT_DEVICE_NAME="/dev/ttyS4";
            }

            //其它的待添加
        }
    }

    /**
     * 视美泰3280型号主板(维冠一体机使用的主板,主要3代机使用)
     */
    interface SHI_MEI_TAI_SMDT{
        interface SerialPortInfoConstant {

            interface SerialPortOne{
                int COM_ID=ComId.ID_ONE;
                String SERIAL_PORT_DEVICE_NAME="/dev/ttyXRM0";
            }
            interface SerialPortTwo{
                int COM_ID=ComId.ID_TWO;
                String SERIAL_PORT_DEVICE_NAME="/dev/ttyXRM1";
            }
            interface SerialPortThree{
                int COM_ID=ComId.ID_THREE;
                String SERIAL_PORT_DEVICE_NAME="/dev/ttyS3";
            }

            //其它的待添加
        }
    }
}
