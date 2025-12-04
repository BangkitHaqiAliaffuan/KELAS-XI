<?php

namespace App\Providers\Filament;

use Filament\Http\Middleware\Authenticate;
use Filament\Http\Middleware\AuthenticateSession;
use Filament\Http\Middleware\DisableBladeIconComponents;
use Filament\Http\Middleware\DispatchServingFilamentEvent;
use Filament\Navigation\NavigationGroup;
use Filament\Pages\Dashboard;
use Filament\Panel;
use Filament\PanelProvider;
use Filament\Support\Colors\Color;
use Filament\Widgets\AccountWidget;
use Illuminate\Cookie\Middleware\AddQueuedCookiesToResponse;
use Illuminate\Cookie\Middleware\EncryptCookies;
use Illuminate\Foundation\Http\Middleware\VerifyCsrfToken;
use Illuminate\Routing\Middleware\SubstituteBindings;
use Illuminate\Session\Middleware\StartSession;
use Illuminate\View\Middleware\ShareErrorsFromSession;

class AdminPanelProvider extends PanelProvider
{
    public function panel(Panel $panel): Panel
    {
        return $panel
            ->default()
            ->id('admin')
            ->path('admin')
            ->login()
            ->registration()
            ->passwordReset()
            ->emailVerification()
            ->profile()
            ->colors([
                'primary' => [
                    50 => '236, 253, 245',
                    100 => '209, 250, 229',
                    200 => '167, 243, 208',
                    300 => '110, 231, 183',
                    400 => '52, 211, 153',
                    500 => '16, 185, 129',
                    600 => '5, 150, 105',
                    700 => '4, 120, 87',
                    800 => '6, 95, 70',
                    900 => '6, 78, 59',
                    950 => '2, 44, 34',
                ],
                'danger' => Color::Rose,
                'gray' => Color::Slate,
                'info' => Color::Sky,
                'success' => Color::Green,
                'warning' => Color::Amber,
            ])
            ->font('Poppins')
            ->brandName('📚 Monitoring Kelas')
            ->brandLogo(null)
            ->favicon(asset('favicon.ico'))
            ->darkMode(true)
            ->sidebarCollapsibleOnDesktop()
            ->sidebarWidth('280px')
            ->navigationGroups([
                NavigationGroup::make()
                    ->label('📊 Akademik')
                    ->icon('heroicon-o-academic-cap')
                    ->collapsed(false),
                NavigationGroup::make()
                    ->label('👥 Manajemen')
                    ->icon('heroicon-o-user-group')
                    ->collapsed(false),
                NavigationGroup::make()
                    ->label('📁 Data Master')
                    ->icon('heroicon-o-folder-open')
                    ->collapsed(false),
            ])
            ->globalSearchKeyBindings(['command+k', 'ctrl+k'])
            ->discoverResources(in: app_path('Filament/Resources'), for: 'App\Filament\Resources')
            ->discoverPages(in: app_path('Filament/Pages'), for: 'App\Filament\Pages')
            ->pages([
                Dashboard::class,
            ])
            ->discoverWidgets(in: app_path('Filament/Widgets'), for: 'App\Filament\Widgets')
            ->widgets([
                AccountWidget::class,
            ])
            ->middleware([
                EncryptCookies::class,
                AddQueuedCookiesToResponse::class,
                StartSession::class,
                AuthenticateSession::class,
                ShareErrorsFromSession::class,
                VerifyCsrfToken::class,
                SubstituteBindings::class,
                DisableBladeIconComponents::class,
                DispatchServingFilamentEvent::class,
            ])
            ->authMiddleware([
                Authenticate::class,
            ]);
    }
}
