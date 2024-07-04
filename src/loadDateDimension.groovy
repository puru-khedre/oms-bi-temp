//def tutorial = ec.entity.makeValue("tutorial.Tutorial")
//tutorial.setFields(context, true, null, null)
//if (!tutorial.tutorialId) tutorial.setSequencedIdPrimary()
//tutorial.create()

//logger.warn("The request body for webhook ${webhookTopic} is empty for Shopify ${shopDomain}, cannot verify webhook")
//response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The Request Body is empty for Shopify webhook")
//return

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat

Logger logger = LoggerFactory.getLogger("temp")

logger.info("++++++++++++++++++++  Generating the date dimension fromDate: ${fromDate} to the thruDate: ${thruDate}")

SimpleDateFormat monthNameFormat = new SimpleDateFormat("MMMM");
SimpleDateFormat dayNameFormat = new SimpleDateFormat("EEEE");
SimpleDateFormat dayDescriptionFormat = new SimpleDateFormat("MMMM d, yyyy");
SimpleDateFormat yearMonthDayFormat = new SimpleDateFormat("yyyy-MM-dd");
SimpleDateFormat yearMonthFormat = new SimpleDateFormat("yyyy-MM");

calendar = Calendar.getInstance()
calendar.setTime(fromDate)
calendar.set(Calendar.HOUR, 0)
calendar.set(Calendar.MINUTE, 0)
calendar.set(Calendar.SECOND, 0)
calendar.set(Calendar.MILLISECOND, 0)

java.sql.Date currentDate = new java.sql.Date(calendar.getTimeInMillis());

while(currentDate.compareTo(thruDate)<=0) {
    dateValue = null

    dateValue = ec.entity.find("bi.DateDimension").condition("dateValue", currentDate).one()

    boolean newValue = (dateValue== null)
    logger.info("++++++++++++++++++++++++ dateValue exists +++++++++++++++++++")

    if(newValue) {
        dateValue = ec.entity.makeValue("bi.DateDimension")
        dateValue.setSequencedIdPrimary()
        dateValue.set("dateValue", new java.sql.Date(currentDate.getTime()))
        logger.info("++++++++++++++++++++++++ dateValue ${dateValue} +++++++++++++++++++")
    }

    dateValue.set("description", dayDescriptionFormat.format(currentDate));
    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
    dateValue.set("dayName", dayNameFormat.format(currentDate));
    dateValue.set("dayOfMonth", Long.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
    dateValue.set("dayOfYear", Long.valueOf(calendar.get(Calendar.DAY_OF_YEAR)));
    dateValue.set("monthName", monthNameFormat.format(currentDate));

    dateValue.set("monthOfYear", Long.valueOf(calendar.get(Calendar.MONTH) + 1));
    dateValue.set("yearName", Long.valueOf(calendar.get(Calendar.YEAR)));
    dateValue.set("weekOfMonth", Long.valueOf(calendar.get(Calendar.WEEK_OF_MONTH)));
    dateValue.set("weekOfYear", Long.valueOf(calendar.get(Calendar.WEEK_OF_YEAR)));
    dateValue.set("weekdayType", (dayOfWeek == 1 || dayOfWeek == 7 ? "Weekend" : "Weekday"));
    dateValue.set("yearMonthDay", yearMonthDayFormat.format(currentDate));
    dateValue.set("yearAndMonth", yearMonthFormat.format(currentDate));

    logger.info("+++++++++++++++++++++++++++++++++ type of dateValue ${dateValue.getClass().getName()} ++++++++++++++++")
    dateValue.createOrUpdate()

    calendar.add(Calendar.DATE, 1)
    currentDate = new java.sql.Date(calendar.getTimeInMillis())
}
return