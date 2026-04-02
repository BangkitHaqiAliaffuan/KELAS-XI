import React, { useEffect, useState } from 'react'
import emailjs from '@emailjs/browser'
import SpaceScroll from './SpaceScroll'
import './Home.css'
import PhotoProfile from '../assets/photo-profile.png'
import CryptoTrackingImage from '../assets/projects/img/CryptoTracking.png'
import TrashcareImage from '../assets/projects/img/Trashcare.png'
import MonitoringKelasImage from '../assets/projects/img/MonitoringKelas.png'
import {
  FaCode,
  FaPaintBrush,
  FaCogs,
  FaTools,
  FaComments,
  FaUsers,
  FaPuzzlePiece,
  FaSyncAlt,
} from 'react-icons/fa'

const Home = () => {
  const [isSending, setIsSending] = useState(false)
  const [sendStatus, setSendStatus] = useState({ type: '', message: '' })
  const [isMobileViewport] = useState(() => window.matchMedia('(max-width: 900px)').matches)

  const emailjsServiceId = String(import.meta.env.VITE_EMAILJS_SERVICE_ID || '').trim()
  const emailjsTemplateId = String(import.meta.env.VITE_EMAILJS_TEMPLATE_ID || '').trim()
  const emailjsPublicKey = String(import.meta.env.VITE_EMAILJS_PUBLIC_KEY || '').trim()

  const handleNavScroll = (event) => {
    const targetId = event.currentTarget.getAttribute('href')

    if (!targetId || !targetId.startsWith('#')) {
      return
    }

    const targetSection = document.querySelector(targetId)

    if (!targetSection) {
      return
    }

    event.preventDefault()
    targetSection.scrollIntoView({ behavior: 'smooth', block: 'start' })
    window.history.replaceState(null, '', targetId)
  }

  useEffect(() => {
    const animatedElements = Array.from(document.querySelectorAll('.reveal'))

    if (!animatedElements.length) {
      return undefined
    }

    const reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches

    if (reducedMotion) {
      animatedElements.forEach((element) => element.classList.add('in-view'))
      return undefined
    }

    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            entry.target.classList.add('in-view')
            observer.unobserve(entry.target)
          }
        })
      },
      {
        threshold: 0.16,
        rootMargin: '0px 0px -8% 0px',
      }
    )

    animatedElements.forEach((element) => observer.observe(element))

    return () => observer.disconnect()
  }, [])

  const handleContactSubmit = async (event) => {
    event.preventDefault()

    const form = event.currentTarget
    const formData = new FormData(form)
    const senderName = String(formData.get('name') || '').trim()
    const senderEmail = String(formData.get('email') || '').trim()
    const senderMessage = String(formData.get('message') || '').trim()

    if (!senderName || !senderEmail || !senderMessage) {
      setSendStatus({
        type: 'error',
        message: 'Please complete all fields before sending.',
      })
      return
    }

    if (!emailjsServiceId || !emailjsTemplateId || !emailjsPublicKey) {
      setSendStatus({
        type: 'error',
        message: 'Email service is not configured yet. Please set the EmailJS environment variables.',
      })
      return
    }

    setIsSending(true)
    setSendStatus({ type: '', message: '' })

    try {
      await emailjs.send(
        emailjsServiceId,
        emailjsTemplateId,
        {
          from_name: senderName,
          reply_to: senderEmail,
          from_email: senderEmail,
          message: senderMessage,
        },
        {
          publicKey: emailjsPublicKey,
        }
      )

      setSendStatus({
        type: 'success',
        message: 'Message sent successfully. Thank you!',
      })
      form.reset()
    } catch (error) {
      const statusCode = error?.status || error?.statusCode
      const errorText = String(error?.text || error?.message || '').trim()
      let friendlyMessage = 'Failed to send message. Please try again in a moment.'

      if (statusCode === 400) {
        friendlyMessage = 'EmailJS rejected request (400). Check template variables and allowed origin in EmailJS settings.'
      }

      if (errorText) {
        friendlyMessage = `${friendlyMessage} (${errorText})`
      }

      console.error('EmailJS send failed:', {
        statusCode,
        errorText,
      })

      setSendStatus({
        type: 'error',
        message: friendlyMessage,
      })
    } finally {
      setIsSending(false)
    }
  }

  return (
    <div className='portfolio-page'>
      {!isMobileViewport ? <SpaceScroll /> : null}

      <div className='sticky-nav reveal reveal-up' style={{ '--reveal-delay': '50ms' }}>
        <nav className='top-nav'>
          <a href='#about' className='brand' onClick={handleNavScroll}>HAQI.SPACE</a>
          <div className='nav-links'>
            <a href='#about' onClick={handleNavScroll}>HOME</a>
            <a href='#about' onClick={handleNavScroll}>ABOUT</a>
            <a href='#projects' onClick={handleNavScroll}>PROJECTS</a>
            <a href='#contact' onClick={handleNavScroll}>CONTACT</a>
          </div>
          <div className='nav-stars' aria-hidden='true'>✦ ✧</div>
        </nav>
      </div>

      <section className='content-section reveal reveal-up' id='about'>
        <div className='about-grid'>
          <div className='reveal reveal-left'>
            <p className='section-index reveal reveal-up' style={{ '--reveal-delay': '60ms' }}>01 / ARCHITECTURE VOID</p>
            <h2 className='reveal reveal-up' style={{ '--reveal-delay': '120ms', 'fontFamily':'Dune Rise', 'fontWeight':'bold'  }}>
              Bangkit Haqi Aliaffuan
              <br />
              <span>Software Engineer</span>
            </h2>
            <p className='reveal reveal-up' style={{ '--reveal-delay': '180ms' }}>
              I am a technology enthusiast who always stays up-to-date with the latest developments and is
              ready to quickly adapt to emerging innovations. I have a passion for using logic to solve
              complex problems, while staying open to long-term opportunities that create real impact.
            </p>
            <p className='reveal reveal-up' style={{ '--reveal-delay': '220ms' }}>
              I completed Full Course Learning Coding at IT Brain Software House, served as Project Manager in
              DIGIFORWARD 2025, and currently work as a Freelance Software Developer with AI-assisted workflow
              and practical delivery for web and mobile solutions.
            </p>
            <div className='stat-row'>
              <div className='reveal reveal-up' style={{ '--reveal-delay': '260ms' }}>
                <strong>10+</strong>
                <span>Apps Delivered</span>
              </div>
              <div className='reveal reveal-up' style={{ '--reveal-delay': '320ms' }}>
                <strong>2</strong>
                <span>Competition Awards</span>
              </div>
              <div className='reveal reveal-up' style={{ '--reveal-delay': '380ms' }}>
                <strong>2024</strong>
                <span>Freelance Start</span>
              </div>
            </div>
          </div>

          <div className='about-panel reveal reveal-right' style={{ '--reveal-delay': '200ms' }}>
            <div className='about-panel__badge'>EDUCATION: SMK NEGERI 2 BUDURAN</div>
            <div className='about-panel__core'>
              <img src={PhotoProfile} alt='Bangkit Haqi Aliaffuan' className='about-panel__photo' />
              <span className='about-panel__scan-wipe' aria-hidden='true' />
              <span className='about-panel__scan-line' aria-hidden='true' />
              <span className='about-panel__tag about-panel__tag--left'>SYS ONLINE</span>
              <span className='about-panel__tag about-panel__tag--right'>ARCHIVE VOID</span>
            </div>
          </div>
        </div>

        <div className='skill-constellation reveal reveal-up'>
          <p className='section-index skill-index reveal reveal-up' style={{ '--reveal-delay': '40ms' }}>02 / CORE CAPABILITIES</p>
          <h3 className='reveal reveal-up' style={{ '--reveal-delay': '120ms','fontFamily':'Dune Rise', 'fontWeight':'bold', 'marginTop':'10px'  }}>Skill Constellations</h3>

          <div className='solar-skill-system reveal reveal-zoom' style={{ '--reveal-delay': '180ms' }}>
            <div className='orbit-ring orbit-ring--inner' aria-hidden='true' />
            <div className='orbit-ring orbit-ring--outer' aria-hidden='true' />

            <div className='solar-core'>
              <FaCogs className='solar-core__icon' />
              <span>SYSTEMS</span>
              <strong>CORE</strong>
            </div>

            {/* Inner orbit — radius 230px */}
            <div
              className='orbit-node orbit-node--hard'
              style={{ transform: 'rotate(330deg)', '--radius': '230px', '--duration': '26s', '--delay': '-2s' }}
            >
              <article className='orbit-skill-card'>
                <FaCode className='skill-card__icon-svg' />
                <small>Frontend</small>
                <strong>Fullstack Web Development</strong>
              </article>
            </div>

            <div
              className='orbit-node orbit-node--hard'
              style={{ transform: 'rotate(30deg)', '--radius': '230px', '--duration': '26s', '--delay': '-7s' }}
            >
              <article className='orbit-skill-card'>
                <FaPaintBrush className='skill-card__icon-svg' />
                <small>Design</small>
                <strong>UI/UX Design</strong>
              </article>
            </div>

            <div
              className='orbit-node orbit-node--hard'
              style={{ transform: 'rotate(150deg)', '--radius': '230px', '--duration': '26s', '--delay': '-11s' }}
            >
              <article className='orbit-skill-card'>
                <FaCogs className='skill-card__icon-svg' />
                <small>Operations</small>
                <strong>DevOps Basic Skill</strong>
              </article>
            </div>

            <div
              className='orbit-node orbit-node--hard'
              style={{ transform: 'rotate(210deg)', '--radius': '230px', '--duration': '26s', '--delay': '-16s' }}
            >
              <article className='orbit-skill-card'>
                <FaTools className='skill-card__icon-svg' />
                <small>Tools</small>
                <strong>Git / VS Code / Postman</strong>
              </article>
            </div>

            {/* Outer orbit — radius 380px */}
            <div
              className='orbit-node orbit-node--soft'
              style={{ transform: 'rotate(0deg)', '--radius': '380px', '--duration': '38s', '--delay': '-5s' }}
            >
              <article className='orbit-skill-card orbit-skill-card--soft'>
                <FaComments className='skill-card__icon-svg' />
                <small>Synergy</small>
                <strong>Communication</strong>
              </article>
            </div>

            <div
              className='orbit-node orbit-node--soft'
              style={{ transform: 'rotate(90deg)', '--radius': '380px', '--duration': '38s', '--delay': '-10s' }}
            >
              <article className='orbit-skill-card orbit-skill-card--soft'>
                <FaUsers className='skill-card__icon-svg' />
                <small>Navigation</small>
                <strong>Leadership</strong>
              </article>
            </div>

            <div
              className='orbit-node orbit-node--soft'
              style={{ transform: 'rotate(180deg)', '--radius': '380px', '--duration': '38s', '--delay': '-20s' }}
            >
              <article className='orbit-skill-card orbit-skill-card--soft'>
                <FaPuzzlePiece className='skill-card__icon-svg' />
                <small>Resolution</small>
                <strong>Problem Solving</strong>
              </article>
            </div>

            <div
              className='orbit-node orbit-node--soft'
              style={{ transform: 'rotate(270deg)', '--radius': '380px', '--duration': '38s', '--delay': '-27s' }}
            >
              <article className='orbit-skill-card orbit-skill-card--soft'>
                <FaSyncAlt className='skill-card__icon-svg' />
                <small>Expansion</small>
                <strong>Adaptability</strong>
              </article>
            </div>
          </div>

          <div className='skill-legend reveal reveal-up' style={{ '--reveal-delay': '300ms' }}>
            <span><i className='legend-dot legend-dot--hard' /> Technical Hard Skills</span>
            <span><i className='legend-dot legend-dot--soft' /> Synergistic Soft Skills</span>
          </div>
        </div>
      </section>

      <section className='content-section reveal reveal-up' id='projects'>
        <div className='section-header reveal reveal-up' style={{ '--reveal-delay': '70ms' }}>
          <p  className='section-index'>03 / PROJECTS</p>
          <h2 style={{ 'fontFamily':'Dune Rise', 'fontWeight':'bold'  }}>Selected Works</h2>
        </div>
        <div className='project-grid'>
          <a
            className='project-card project-card--featured project-card--link reveal reveal-up'
            style={{ '--reveal-delay': '160ms' }}
            href='https://cryptotracking-mu.vercel.app/'
            target='_blank'
            rel='noreferrer noopener'
            aria-label='Open Crypto Price Tracking Website'
          >
            <div className='project-media'>
              <img src={CryptoTrackingImage} alt='Crypto Price Tracking Website' />
              <span className='project-hover-cta' aria-hidden='true'>Kunjungi Website ↗</span>
              <div className='project-overlay'>
                <h3>Crypto Price Tracking Website</h3>
                <p>Website pelacakan harga cryptocurrency real-time dengan pencarian coin, watchlist, dan detail aset yang dipersonalisasi.</p>
                <small>WEB APP • REAL-TIME DATA</small>
              </div>
            </div>
          </a>
          <article className='project-card reveal reveal-up' style={{ '--reveal-delay': '240ms' }}>
            <div className='project-media'>
              <img src={TrashcareImage} alt='TrashCare: Smart Waste Pickup & Reuse Marketplace' />
              <div className='project-overlay'>
                <h3>TrashCare: Smart Waste Pickup & Reuse Marketplace</h3>
                <p>Aplikasi mobile untuk pickup sampah gratis dan marketplace barang bekas guna mendorong circular use di komunitas.</p>
                <small>MOBILE APP • SUSTAINABILITY</small>
              </div>
            </div>
          </article>
          <article className='project-card reveal reveal-up' style={{ '--reveal-delay': '320ms' }}>
            <div className='project-media'>
              <img src={MonitoringKelasImage} alt='Class Monitoring Application for School Operations' />
              <div className='project-overlay'>
                <h3>Class Monitoring Application for School Operations</h3>
                <p>Aplikasi monitoring kelas untuk attendance, jadwal, tugas, dan progress siswa agar operasional sekolah lebih rapi dan efisien.</p>
                <small>EDTECH • OPERATIONS</small>
              </div>
            </div>
          </article>
        </div>
      </section>

      <section className='content-section contact reveal reveal-up' id='contact'>
        <div className='contact-grid'>
          <div className='reveal reveal-left' style={{ '--reveal-delay': '120ms' }}>
            <p className='section-index'>04 / CONTACT</p>
            <h2 style={{ 'fontFamily':'Dune Rise', 'fontWeight':'bold'  }}>Let&apos;s Connect.</h2>
            <p>
              Open for collaboration, project opportunities, and software development partnerships.
            </p>
            <ul className='contact-meta'>
              <li>Phone: 0822-2935-4434</li>
              <li>Email: bangkithaqialiafuan@gmail.com</li>
              <li>Address: Taman Apsari, MCA Blok P1-39, Sidoarjo 61272</li>
            </ul>
          </div>

          <form className='contact-form reveal reveal-right' style={{ '--reveal-delay': '180ms' }} onSubmit={handleContactSubmit}>
            <label htmlFor='identity'>Name</label>
            <input id='identity' name='name' placeholder='Your name' required />

            <label htmlFor='signal'>Email</label>
            <input id='signal' name='email' type='email' placeholder='your@email.com' required />

            <label htmlFor='message'>Message</label>
            <textarea id='message' name='message' rows='4' placeholder='Write your message...' required />

            <button type='submit' disabled={isSending}>{isSending ? 'SENDING...' : 'SEND MESSAGE'}</button>
            {sendStatus.message ? (
              <p className={`contact-form__status contact-form__status--${sendStatus.type}`} role='status'>
                {sendStatus.message}
              </p>
            ) : null}
          </form>
        </div>
      </section>

      <footer className='site-footer reveal reveal-up' style={{ '--reveal-delay': '120ms' }}>
        <div className='footer-grid'>
          <div className='footer-brand'>
            <a href='#about' className='brand' onClick={handleNavScroll}>HAQI.SPACE</a>
            <p>
              Portfolio of Bangkit Haqi Aliaffuan — software engineer focused on practical,
              impactful digital products.
            </p>
          </div>

          <div className='footer-col'>
            <h4>Navigate</h4>
            <a href='#about' onClick={handleNavScroll}>About</a>
            <a href='#projects' onClick={handleNavScroll}>Projects</a>
            <a href='#contact' onClick={handleNavScroll}>Contact</a>
          </div>

          <div className='footer-col'>
            <h4>Contact</h4>
            <p>0822-2935-4434</p>
            <p>bangkithaqialiafuan@gmail.com</p>
            <p>Sidoarjo, Indonesia</p>
          </div>

          <div className='footer-col'>
            <h4>Availability</h4>
            <p>Open for freelance and collaboration projects.</p>
            <a href='#contact' className='footer-cta' onClick={handleNavScroll}>Start a Project</a>
          </div>
        </div>

        <div className='footer-bottom'>
          <span>© 2026 Bangkit Haqi Aliaffuan</span>
          <span>Built with React + GSAP</span>
        </div>
      </footer>
    </div>
  )
}

export default Home