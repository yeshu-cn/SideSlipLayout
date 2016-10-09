## SideSlipLayout

可滑动显示菜单的Layout

![](https://github.com/yeshu-cn/SideSlipLayout/blob/master/demo.gif)



## Usage

* 必须包含`android:id="@+id/side_slip_menu"`和`android:id="@+id/side_slip_content"`
* `android:id="@+id/side_slip_menu"`中显示菜单
* `android:id="@+id/side_slip_content"`中显示内容

```xml
<work.yeshu.sidesliplayout.SideSlipLayout
        android:id="@+id/side_slip_layout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@color/colorAccent">

        <LinearLayout
            android:id="@+id/side_slip_menu"
            android:layout_width="100dp"
            android:layout_height="match_parent"
            android:layout_gravity="end">
		
		    <!-- 菜单布局 -->	
        </LinearLayout>

        <LinearLayout
            android:id="@+id/side_slip_content"
            android:layout_width="match_parent"
            android:layout_height="200dp">
			
		    <!-- 内容布局 -->	
        </LinearLayout>
    </work.yeshu.sidesliplayout.SideSlipLayout>
``` 
