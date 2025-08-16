import React from "react";
import "./Contact.css";
import { assets } from "../../assets/assets";
const Contact = () => {
    const [result, setResult] = React.useState("");

  const onSubmit = async (event) => {
    event.preventDefault();
    setResult("Sending....");
    const formData = new FormData(event.target);

    formData.append("access_key", "fe3662fc-0786-4b28-8c41-9769f63d6517");

    const response = await fetch("https://api.web3forms.com/submit", {
      method: "POST",
      body: formData
    });

    const data = await response.json();

    if (data.success) {
      setResult("Form Submitted Successfully");
      event.target.reset();
    } else {
      console.log("Error", data);
      setResult(data.message);
    }
  };

  return (
    <div className="contact">
      <div className="contact-col">
        <h3>
          Send us a message
          <img src={assets.msg_icon} />
        </h3>
        <p>
          Feel free to reach out to us through this contact form or you can
          contact us through the following information below. We're here to help
          and answer any questions you may have about our programs, admissions,
          or campus life.
        </p>
        <ul>
          <li>
            <img src={assets.mail_icon}/>
            admissions@edusityuniv.edu</li>
          <li><img src={assets.phone_icon_icon}/>+1 (555) 123-4567</li>
          <li>
            <img src={assets.location_icon}/>
            123 University Ave, Cambridge, MA 02138, USA</li>
        </ul>
      </div>
      <div className="contact-col">
        <form onClick={onSubmit}>
            <label>Your Name</label>
            <input type="text" name="name" placeholder="Enter Your Name" required />
                <label>Phone Number</label>
                <input type="tel" name="phone" placeholder="Enter your mobile phone number" required/>
                <label>Write your messages here</label>
                <textarea name="message" rows="6" placeholder="Enter your message" required/>
                <button type="submit" className="btn dark-btn">Submit Now
                    <img src={assets.white_arrow}/>
                </button>
        </form>
        <span>{result}</span>
      </div>
    </div>
  );
};

export default Contact;
