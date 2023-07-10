package mp.app.calonex.landlord.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_unit_add.*
import mp.app.calonex.R
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.AddNewPropertyFirstScreen.Companion.result

class UnitAddActivity : CxBaseActivity2() {

    var linearLayout: LinearLayout? = null

    private lateinit var add_unit_finish: Button

    private lateinit var unit_add_skip: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unit_add)
        supportActionBar?.hide()

        linearLayout = findViewById(R.id.units_ll)
        add_unit_finish=findViewById(R.id.add_unit_finish);
        val vi: LayoutInflater? = layoutInflater
        var numberOfUnit = result.numberOfUnits!!.toInt()
        unit_add_skip=findViewById(R.id.unit_add_skip);

        startHandler()


        for (i in 1..numberOfUnit){
            val view: View? = vi?.inflate(R.layout.item_units_add_property, linearLayout, false)
            var unitNumber : TextView? = view?.findViewById(R.id.unit_number)

            unitNumber?.setText("Unit "+i)

            units_ll.addView(view)

            unitNumber?.setOnClickListener { _ ->
                val intent = Intent(this, NewUnitDescriptionScreen::class.java)
                intent.putExtra("unitNumber",i)
                startActivityForResult(intent, 1)
            }

        }

        add_unit_finish.setOnClickListener {
            val intent = Intent (this, AddNewPropertyThirdScreen::class.java)
            startActivity(intent)

//            val intent = Intent (this, HomeActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startActivity(intent)
//            finish()
        }

        unit_add_skip.setOnClickListener {
            val intent = Intent (this, AddNewPropertyThirdScreen::class.java)
            startActivity(intent)
        }

    }

    override fun onBackPressed() {
        val intent = Intent (this, HomeActivityCx::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        Util.navigationBack(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                if (data != null) {
                    data.getStringExtra("result")
                }
                  Toast.makeText(applicationContext,"Unit added Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(applicationContext,"Unit added failed",Toast.LENGTH_SHORT).show()

            }
        }
    }


}
